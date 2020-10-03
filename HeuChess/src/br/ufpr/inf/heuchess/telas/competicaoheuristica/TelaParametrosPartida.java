package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.Historico.Tipo;
import br.ufpr.inf.heuchess.competicaoheuristica.AvaliadorDHJOG;
import br.ufpr.inf.heuchess.competicaoheuristica.Engine;
import br.ufpr.inf.heuchess.competicaoheuristica.EngineAlphaBetaNega;
import br.ufpr.inf.heuchess.competicaoheuristica.EngineMiniMax;
import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.competicaoheuristica.Jogador;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.ConjuntoHeuristicoDAO;
import br.ufpr.inf.heuchess.persistencia.SituacaoJogoDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.iniciais.RenderTreeObjetos;
import br.ufpr.inf.heuchess.telas.situacaojogo.DesenhaSituacaoJogo;
import br.ufpr.inf.heuchess.telas.situacaojogo.TreeModelConjuntosHeuristicos;
import br.ufpr.inf.heuchess.telas.situacaojogo.TreeModelSituacoesJogo;
import br.ufpr.inf.utils.ArquivoLog;
import br.ufpr.inf.utils.gui.*;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 11, 2012
 */
public class TelaParametrosPartida extends javax.swing.JFrame implements ModalFrameHierarchy {
    
    private CardLayout card;
    private int        etapaCriacao;    
    
    private TreeModelConjuntosHeuristicos treeModelConjuntosHeuristicos1, treeModelConjuntosHeuristicos2;
    private TreeModelSituacoesJogo        treeModelSituacoesJogo;
    
    private AcessoTelaParametrosPartida acessoTelaParametrosPartida;
    
    private DesenhaSituacaoJogo desenhaSituacaoJogo = new DesenhaSituacaoJogo(this);
        
    /**
     * Construtor chamado sem nenhum Conjunto Heurístico definido
     */
    public TelaParametrosPartida(AcessoTelaParametrosPartida acessoTelaParametrosPartida) {
        
        try{
            inicializacaoBasica(acessoTelaParametrosPartida);
        }catch(Exception e){            
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(acessoTelaParametrosPartida.getFrame(),"Erro ao carregar dados!\n" + e.getMessage());
            dispose();
            acessoTelaParametrosPartida.fechandoTelaParametrosPartida(null);
            return;
        }
        
        ModalFrameUtil.showAsModalDontBlock(this); 
    }

    /**
     * Construtor chamado com um ElementoLista já escolhido
     */
    public TelaParametrosPartida(AcessoTelaParametrosPartida acessoTelaParametrosPartida, Class classe, long id) {
        
        try{
            inicializacaoBasica(acessoTelaParametrosPartida);
        }catch(Exception e){
            HeuChess.registraExcecao(e);            
            UtilsGUI.dialogoErro(acessoTelaParametrosPartida.getFrame(),"Erro ao carregar dados!\n" + e.getMessage());
            dispose();
            acessoTelaParametrosPartida.fechandoTelaParametrosPartida(null);
            return;
        }
        
        if (classe == ConjuntoHeuristico.class){
            
            if (treeModelConjuntosHeuristicos1.selecionaTreeNode(classe, id)){

                etapaCriacao = 2;
        
                card.show(jPanelPrincipal,"Passo2");
        
                jButtonAnterior.setVisible(true);
                jButtonProximo.setEnabled(false);
            }
            
        }else
            if (classe == SituacaoJogo.class){    
                
                treeModelSituacoesJogo.selecionaTreeNode(classe, id);
            
            }else{                
                UtilsGUI.dialogoErro(acessoTelaParametrosPartida.getFrame(),"Tipo de elemento da Lista não suportado por este Construtor [" + classe.getName() + "]");
                dispose();
                acessoTelaParametrosPartida.fechandoTelaParametrosPartida(null);
                return;
            }
        
        ModalFrameUtil.showAsModalDontBlock(this); 
    }
    
    private void inicializacaoBasica(AcessoTelaParametrosPartida acessoTelaParametrosPartida) throws Exception {
        
        etapaCriacao = 1;
        
        this.acessoTelaParametrosPartida = acessoTelaParametrosPartida;        
        
        desenhaSituacaoJogo.setEditavel(false);
        
        initComponents();
        
        treeModelConjuntosHeuristicos1 = new TreeModelConjuntosHeuristicos(jTreeConjuntoHeuristicos1);
        treeModelConjuntosHeuristicos2 = new TreeModelConjuntosHeuristicos(jTreeConjuntoHeuristicos2);
        treeModelSituacoesJogo         = new TreeModelSituacoesJogo(jTreeSituacoesJogo);
        
        treeModelConjuntosHeuristicos1.forceUpdate();
        treeModelConjuntosHeuristicos1.expandMeusConjuntos();
        
        treeModelConjuntosHeuristicos2.forceUpdate();
        treeModelConjuntosHeuristicos2.expandMeusConjuntos();
        
        treeModelSituacoesJogo.forceUpdate();
        treeModelSituacoesJogo.expandPadrao();        
        treeModelSituacoesJogo.expandMinhasSituacoes();
        
        desenhaSituacaoJogo.setSize(jPanelTabuleiro.getWidth(),jPanelTabuleiro.getHeight());
        
        card = (CardLayout) jPanelPrincipal.getLayout();                
        
        jButtonAnterior.setVisible(false);
        jButtonProximo.setEnabled(false);
        
        jLabelBoxEngine.setVisible(false);
        jComboBoxEngine.setVisible(false);        
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaParametrosPartida;
    }
    
    private void fechar(){
        dispose();        
        acessoTelaParametrosPartida.fechandoTelaParametrosPartida(null);
    }        
         
    private void verificaTreeConjuntoHeuristico(TreeModelConjuntosHeuristicos treeModel) {

        if (treeModel.recuperaElementoLista(ConjuntoHeuristico.class) != null) {
            jButtonProximo.setEnabled(true);
        }else{
            jButtonProximo.setEnabled(false);
        }
    }
    
    private void verificaTreeSituacoesJogo(){

        ElementoLista elementoSituacaoJogo = treeModelSituacoesJogo.recuperaElementoLista(SituacaoJogo.class);

        if (elementoSituacaoJogo != null) {

            jButtonProximo.setEnabled(true);

            desenhaSituacaoJogo.configura(elementoSituacaoJogo.getDescricao());

            jPanelTabuleiro.setVisible(true);
            jTextFieldFEN.setText(elementoSituacaoJogo.getDescricao());
            jLabelDescricaoVantagem.setText(elementoSituacaoJogo.getTipo().getDescricao());
            
        }else{
            jButtonProximo.setEnabled(false);
            jPanelTabuleiro.setVisible(false);
            jTextFieldFEN.setText("");
            jLabelDescricaoVantagem.setText("");
        }
    }
    
    private void avancarEtapa(){
        
        if (jButtonProximo.getText().equalsIgnoreCase("Próximo")){
            
            etapaCriacao++;
            
            ativaAtualizacoesTree(true);
            
            switch(etapaCriacao){
                
                case 2:card.show(jPanelPrincipal,"Passo2");
                    jButtonAnterior.setVisible(true);
                    verificaTreeConjuntoHeuristico(treeModelConjuntosHeuristicos2);
                    break;
                    
                case 3:card.show(jPanelPrincipal,"Passo3");
                    verificaTreeSituacoesJogo();
                    break;
                    
                case 4:card.show(jPanelPrincipal,"Passo4");
                    jButtonProximo.setText("Concluir");
                    jButtonProximo.setMnemonic('n');
                    jButtonProximo.setEnabled(true);
                    jTextFieldConjunto1.setText(treeModelConjuntosHeuristicos1.recuperaElementoLista(ConjuntoHeuristico.class).getNome());
                    jTextFieldConjunto2.setText(treeModelConjuntosHeuristicos2.recuperaElementoLista(ConjuntoHeuristico.class).getNome());
                    break;
                    
                default:    
                    UtilsGUI.dialogoErro(this,"Etapa inválida de criação!");
            }
            
        }else{            
            criaPartida();
        }
    }
    
    private void criaPartida() {
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        ConjuntoHeuristico conjunto1, conjunto2;

        try {
            conjunto1 = ConjuntoHeuristicoDAO.busca(treeModelConjuntosHeuristicos1.recuperaElementoLista(ConjuntoHeuristico.class).getId());
            conjunto2 = ConjuntoHeuristicoDAO.busca(treeModelConjuntosHeuristicos2.recuperaElementoLista(ConjuntoHeuristico.class).getId());
            
        } catch (Exception ex) {
            HeuChess.registraExcecao(ex);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao carregar o Conjunto Heurístico!\n" + ex.getMessage());
            return;
        }

        Tabuleiro tabuleiroInicial;

        try {
            tabuleiroInicial = new Tabuleiro(jTextFieldFEN.getText());

        } catch (Exception e) {          
            HeuChess.registraExcecao(e);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao carregar o Tabuleiro Inicial da Partida!\n" + e.getMessage());
            return;
        }

        int profConjunto1 = jSliderProfundidadeConjunto1.getValue();
        int profConjunto2 = jSliderProfundidadeConjunto2.getValue();
        
        Engine engineJogador1, engineJogador2;

        ArquivoLog arquivoLog = null;

        try {
            //arquivoLog = ArquivoLog.createArquivoLog("./logs/", "Partida de Xadrez ", "HeuChess", false);

            if (!jComboBoxEngine.isVisible() || jComboBoxEngine.getSelectedIndex() == 0) {
                engineJogador1 = new EngineAlphaBetaNega(new AvaliadorDHJOG(conjunto1, arquivoLog), profConjunto1, arquivoLog);
                engineJogador2 = new EngineAlphaBetaNega(new AvaliadorDHJOG(conjunto2, arquivoLog), profConjunto2, arquivoLog);
            } else {
                engineJogador1 = new EngineMiniMax(new AvaliadorDHJOG(conjunto1, arquivoLog), profConjunto1, arquivoLog);
                engineJogador2 = new EngineMiniMax(new AvaliadorDHJOG(conjunto2, arquivoLog), profConjunto2, arquivoLog);
            }

        } catch (Exception e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(this, "Erro ao criar Avaliador Heurístico!\n" + e.getMessage());
            return;
        }

        Jogador jogadorBrancas, jogadorPretas;

        boolean primeiroConjuntoBrancas = (jComboBoxCorConjunto1.getSelectedIndex() == 0);
                
        if (primeiroConjuntoBrancas) {
            jogadorBrancas = new Jogador(conjunto1, true);
            jogadorBrancas.setEngine(engineJogador1);
            jogadorPretas  = new Jogador(conjunto2, false);
            jogadorPretas.setEngine(engineJogador2);
        } else {
            jogadorBrancas = new Jogador(conjunto2, true);
            jogadorBrancas.setEngine(engineJogador2);
            jogadorPretas  = new Jogador(conjunto1, false);
            jogadorPretas.setEngine(engineJogador1);
        }
        
        final Partida game;

        try {
            game = new Partida(jogadorBrancas, jogadorPretas, tabuleiroInicial);

            game.setPassoAPasso(jRadioButtonModoPassoAPasso.isSelected());
            game.setArquivoLog(arquivoLog);
            //game.setLimiteTempo(1000*30*1,1000*5,1000*60,1000*1,Partida.ClockMode.CLOCK_FISCHER);

            ///////////////////////////////////////////////
            // Registra Histórico de Uso dos Componentes //
            ///////////////////////////////////////////////
            
            Historico.registraComponenteUsado(Tipo.USOU_COMPONENTE_PARTIDA, conjunto1, primeiroConjuntoBrancas ? true : false);
            Historico.registraComponenteUsado(Tipo.USOU_COMPONENTE_PARTIDA, conjunto2, primeiroConjuntoBrancas ? false : true);

            ElementoLista elementoSituacaoJogo = treeModelSituacoesJogo.recuperaElementoLista(SituacaoJogo.class);

            if (elementoSituacaoJogo.getId() != TreeModelSituacoesJogo.COD_TABULEIRO_PADRAO) {

                SituacaoJogo situacaoJogo = SituacaoJogoDAO.busca(elementoSituacaoJogo.getId());

                Historico.registraComponenteUsado(Tipo.USOU_COMPONENTE_PARTIDA, situacaoJogo, false);
            }
            
            ConexaoDBHeuChess.commit();

            dispose();
            acessoTelaParametrosPartida.fechandoTelaParametrosPartida(game);  
        
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao iniciar a Partida!\n" + e.getMessage());
        }
    }
    
    private void ativaAtualizacoesTree(boolean ativar) {
        
        if (ativar) {
            
            switch(etapaCriacao){
                
                case 1:
                    if (treeModelConjuntosHeuristicos1 != null && !treeModelConjuntosHeuristicos1.isExecutando()) {
                        treeModelConjuntosHeuristicos1.start();
                    }    
                    if (treeModelConjuntosHeuristicos2 != null && treeModelConjuntosHeuristicos2.isExecutando()) {
                        treeModelConjuntosHeuristicos2.stop();
                    }
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
                    
                case 2:
                    if (treeModelConjuntosHeuristicos1 != null && treeModelConjuntosHeuristicos1.isExecutando()) {
                        treeModelConjuntosHeuristicos1.stop();
                    }    
                    if (treeModelConjuntosHeuristicos2 != null && !treeModelConjuntosHeuristicos2.isExecutando()) {
                        treeModelConjuntosHeuristicos2.start();
                    }
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
                    
                case 3:
                    if (treeModelConjuntosHeuristicos1 != null && treeModelConjuntosHeuristicos1.isExecutando()) {
                        treeModelConjuntosHeuristicos1.stop();
                    }    
                    if (treeModelConjuntosHeuristicos2 != null && treeModelConjuntosHeuristicos2.isExecutando()) {
                        treeModelConjuntosHeuristicos2.stop();
                    }
                    if (treeModelSituacoesJogo != null && !treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.start();
                    }
                    break;
                    
                case 4:
                    if (treeModelConjuntosHeuristicos1 != null && treeModelConjuntosHeuristicos1.isExecutando()) {
                        treeModelConjuntosHeuristicos1.stop();
                    }            
                    if (treeModelConjuntosHeuristicos2 != null && treeModelConjuntosHeuristicos2.isExecutando()) {
                        treeModelConjuntosHeuristicos2.stop();
                    }
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
            }
            
        } else {
            
            if (treeModelConjuntosHeuristicos1 != null && treeModelConjuntosHeuristicos1.isExecutando()) {
                treeModelConjuntosHeuristicos1.stop();
            }            
            if (treeModelConjuntosHeuristicos2 != null && treeModelConjuntosHeuristicos2.isExecutando()) {
                treeModelConjuntosHeuristicos2.stop();
            }
            if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                treeModelSituacoesJogo.stop();
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupModoExecucao = new javax.swing.ButtonGroup();
        jPanelPrincipal = new javax.swing.JPanel();
        jPanelPasso1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeConjuntoHeuristicos1 = new javax.swing.JTree();
        jPanelPasso2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTreeConjuntoHeuristicos2 = new javax.swing.JTree();
        jPanelPasso3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTreeSituacoesJogo = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFEN = new javax.swing.JTextField();
        jPanelLayoutNull = new javax.swing.JPanel();
        jPanelTabuleiro = new javax.swing.JPanel();
        jPanelTabuleiro.add(desenhaSituacaoJogo);
        jLabelDescricaoVantagem = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanelPasso4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanelPrimeiroConjunto = new javax.swing.JPanel();
        jTextFieldConjunto1 = new javax.swing.JTextField();
        jComboBoxCorConjunto1 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSliderProfundidadeConjunto1 = new javax.swing.JSlider();
        jPanelSegundoConjunto = new javax.swing.JPanel();
        jTextFieldConjunto2 = new javax.swing.JTextField();
        jComboBoxCorConjunto2 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSliderProfundidadeConjunto2 = new javax.swing.JSlider();
        jPanelModoExecucao = new javax.swing.JPanel();
        jRadioButtonModoAutomatico = new javax.swing.JRadioButton();
        jRadioButtonModoPassoAPasso = new javax.swing.JRadioButton();
        jComboBoxEngine = new javax.swing.JComboBox();
        jLabelBoxEngine = new javax.swing.JLabel();
        jButtonCancelar = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Define Parâmetros da Nova Partida");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_competicao.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });

        jPanelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPrincipal.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Passo 1 de 4");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Escolha o PRIMEIRO Conjunto Heurístico");

        jTreeConjuntoHeuristicos1.setShowsRootHandles(true);
        jTreeConjuntoHeuristicos1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeConjuntoHeuristicos1.setCellRenderer(new RenderTreeObjetos(true));
        jTreeConjuntoHeuristicos1.setToggleClickCount(1);
        jTreeConjuntoHeuristicos1.setScrollsOnExpand(true);
        jTreeConjuntoHeuristicos1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeConjuntoHeuristicos1MouseClicked(evt);
            }
        });
        jTreeConjuntoHeuristicos1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeConjuntoHeuristicos1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeConjuntoHeuristicos1);

        javax.swing.GroupLayout jPanelPasso1Layout = new javax.swing.GroupLayout(jPanelPasso1);
        jPanelPasso1.setLayout(jPanelPasso1Layout);
        jPanelPasso1Layout.setHorizontalGroup(
            jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelPasso1Layout.createSequentialGroup()
                        .addGroup(jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel18))
                        .addGap(0, 435, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPasso1Layout.setVerticalGroup(
            jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelPasso1, "Passo1");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Passo 2 de 4");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Escolha o SEGUNDO Conjunto Heurístico");

        jTreeConjuntoHeuristicos2.setShowsRootHandles(true);
        jTreeConjuntoHeuristicos2.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeConjuntoHeuristicos2.setCellRenderer(new RenderTreeObjetos(true));
        jTreeConjuntoHeuristicos2.setToggleClickCount(1);
        jTreeConjuntoHeuristicos2.setScrollsOnExpand(true);
        jTreeConjuntoHeuristicos2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeConjuntoHeuristicos2MouseClicked(evt);
            }
        });
        jTreeConjuntoHeuristicos2.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeConjuntoHeuristicos2ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTreeConjuntoHeuristicos2);

        javax.swing.GroupLayout jPanelPasso2Layout = new javax.swing.GroupLayout(jPanelPasso2);
        jPanelPasso2.setLayout(jPanelPasso2Layout);
        jPanelPasso2Layout.setHorizontalGroup(
            jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel23))
                        .addGap(0, 440, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPasso2Layout.setVerticalGroup(
            jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelPasso2, "Passo2");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Passo 3 de 4");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Defina o tabuleiro inicial");

        jTreeSituacoesJogo.setShowsRootHandles(true);
        jTreeSituacoesJogo.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeSituacoesJogo.setCellRenderer(new RenderTreeObjetos(false));
        jTreeSituacoesJogo.setToggleClickCount(1);
        jTreeSituacoesJogo.setScrollsOnExpand(true);
        jTreeSituacoesJogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeSituacoesJogoMouseClicked(evt);
            }
        });
        jTreeSituacoesJogo.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeSituacoesJogoValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTreeSituacoesJogo);

        jLabel1.setText("Notação FEN (Forsyth-Edwards Notation)");

        jTextFieldFEN.setHorizontalAlignment(JTextField.CENTER);
        jTextFieldFEN.setBackground(new java.awt.Color(255, 255, 204));
        jTextFieldFEN.setEditable(false);

        jPanelLayoutNull.setLayout(null);

        jPanelTabuleiro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelTabuleiro.setMaximumSize(new java.awt.Dimension(330, 280));
        jPanelTabuleiro.setMinimumSize(new java.awt.Dimension(330, 280));
        jPanelTabuleiro.setPreferredSize(new java.awt.Dimension(330, 280));

        javax.swing.GroupLayout jPanelTabuleiroLayout = new javax.swing.GroupLayout(jPanelTabuleiro);
        jPanelTabuleiro.setLayout(jPanelTabuleiroLayout);
        jPanelTabuleiroLayout.setHorizontalGroup(
            jPanelTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 326, Short.MAX_VALUE)
        );
        jPanelTabuleiroLayout.setVerticalGroup(
            jPanelTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        jPanelLayoutNull.add(jPanelTabuleiro);
        jPanelTabuleiro.setBounds(0, 0, 310, 280);

        jLabelDescricaoVantagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel7.setText("    ");

        javax.swing.GroupLayout jPanelPasso3Layout = new javax.swing.GroupLayout(jPanelPasso3);
        jPanelPasso3.setLayout(jPanelPasso3Layout);
        jPanelPasso3Layout.setHorizontalGroup(
            jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso3Layout.createSequentialGroup()
                        .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel12)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPasso3Layout.createSequentialGroup()
                        .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldFEN))))
                .addContainerGap())
        );
        jPanelPasso3Layout.setVerticalGroup(
            jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso3Layout.createSequentialGroup()
                        .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 31, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPasso3Layout.createSequentialGroup()
                        .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldFEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(5, 5, 5)
                        .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanelPrincipal.add(jPanelPasso3, "Passo3");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Passo 4 de 4");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Defina a profundidade de busca do Algoritmo Minimax e a Cor de cada Jogador");

        jPanelPrimeiroConjunto.setBorder(javax.swing.BorderFactory.createTitledBorder("Primeiro Conjunto Heurístico"));

        jTextFieldConjunto1.setEditable(false);

        jComboBoxCorConjunto1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Brancas", "Pretas" }));
        jComboBoxCorConjunto1.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxCorConjunto1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCorConjunto1ItemStateChanged(evt);
            }
        });

        jLabel8.setText("Cor");

        jLabel6.setText("Profundidade de Busca:");

        jSliderProfundidadeConjunto1.setMaximum(EngineMiniMax.PROFUNDIDADE_MAXIMA_BUSCA);
        jSliderProfundidadeConjunto1.setMinimum(EngineMiniMax.PROFUNDIDADE_MINIMA_BUSCA);
        jSliderProfundidadeConjunto1.setMinorTickSpacing(1);
        jSliderProfundidadeConjunto1.setPaintLabels(true);
        jSliderProfundidadeConjunto1.setPaintTicks(true);
        jSliderProfundidadeConjunto1.setSnapToTicks(true);
        jSliderProfundidadeConjunto1.setToolTipText("");
        jSliderProfundidadeConjunto1.setLabelTable(jSliderProfundidadeConjunto1.createStandardLabels(1));
        jSliderProfundidadeConjunto1.setValue(1);

        javax.swing.GroupLayout jPanelPrimeiroConjuntoLayout = new javax.swing.GroupLayout(jPanelPrimeiroConjunto);
        jPanelPrimeiroConjunto.setLayout(jPanelPrimeiroConjuntoLayout);
        jPanelPrimeiroConjuntoLayout.setHorizontalGroup(
            jPanelPrimeiroConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrimeiroConjuntoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelPrimeiroConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPrimeiroConjuntoLayout.createSequentialGroup()
                        .addComponent(jTextFieldConjunto1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxCorConjunto1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPrimeiroConjuntoLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderProfundidadeConjunto1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPrimeiroConjuntoLayout.setVerticalGroup(
            jPanelPrimeiroConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrimeiroConjuntoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelPrimeiroConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldConjunto1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCorConjunto1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelPrimeiroConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jSliderProfundidadeConjunto1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanelSegundoConjunto.setBorder(javax.swing.BorderFactory.createTitledBorder("Segundo Conjunto Heurístico"));

        jTextFieldConjunto2.setEditable(false);

        jComboBoxCorConjunto2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pretas", "Brancas" }));
        jComboBoxCorConjunto2.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxCorConjunto2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCorConjunto2ItemStateChanged(evt);
            }
        });

        jLabel4.setText("Cor");

        jLabel9.setText("Profundidade de Busca:");

        jSliderProfundidadeConjunto2.setMaximum(EngineMiniMax.PROFUNDIDADE_MAXIMA_BUSCA);
        jSliderProfundidadeConjunto2.setMinimum(EngineMiniMax.PROFUNDIDADE_MINIMA_BUSCA);
        jSliderProfundidadeConjunto2.setMinorTickSpacing(1);
        jSliderProfundidadeConjunto2.setPaintLabels(true);
        jSliderProfundidadeConjunto2.setPaintTicks(true);
        jSliderProfundidadeConjunto2.setSnapToTicks(true);
        jSliderProfundidadeConjunto2.setLabelTable(jSliderProfundidadeConjunto2.createStandardLabels(1));
        jSliderProfundidadeConjunto2.setValue(1);

        javax.swing.GroupLayout jPanelSegundoConjuntoLayout = new javax.swing.GroupLayout(jPanelSegundoConjunto);
        jPanelSegundoConjunto.setLayout(jPanelSegundoConjuntoLayout);
        jPanelSegundoConjuntoLayout.setHorizontalGroup(
            jPanelSegundoConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSegundoConjuntoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelSegundoConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSegundoConjuntoLayout.createSequentialGroup()
                        .addComponent(jTextFieldConjunto2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxCorConjunto2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelSegundoConjuntoLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSliderProfundidadeConjunto2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelSegundoConjuntoLayout.setVerticalGroup(
            jPanelSegundoConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSegundoConjuntoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelSegundoConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldConjunto2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCorConjunto2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanelSegundoConjuntoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jSliderProfundidadeConjunto2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanelModoExecucao.setBorder(javax.swing.BorderFactory.createTitledBorder("Modo de Execução da Partida"));

        buttonGroupModoExecucao.add(jRadioButtonModoAutomatico);
        jRadioButtonModoAutomatico.setSelected(true);
        jRadioButtonModoAutomatico.setText("Automático (A partida termina mais rápido e sem a intervenção do usuário)");
        jRadioButtonModoAutomatico.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonModoAutomaticoItemStateChanged(evt);
            }
        });

        buttonGroupModoExecucao.add(jRadioButtonModoPassoAPasso);
        jRadioButtonModoPassoAPasso.setText("Passo a Passo (Permite análise completa da escolha das jogadas)");
        jRadioButtonModoPassoAPasso.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonModoPassoAPassoItemStateChanged(evt);
            }
        });

        jComboBoxEngine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Otimizada", "Completa" }));
        jComboBoxEngine.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        jLabelBoxEngine.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBoxEngine.setText("Realizar Busca");

        javax.swing.GroupLayout jPanelModoExecucaoLayout = new javax.swing.GroupLayout(jPanelModoExecucao);
        jPanelModoExecucao.setLayout(jPanelModoExecucaoLayout);
        jPanelModoExecucaoLayout.setHorizontalGroup(
            jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModoExecucaoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonModoAutomatico, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                    .addComponent(jRadioButtonModoPassoAPasso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxEngine, 0, 111, Short.MAX_VALUE)
                    .addComponent(jLabelBoxEngine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanelModoExecucaoLayout.setVerticalGroup(
            jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModoExecucaoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonModoAutomatico)
                    .addComponent(jLabelBoxEngine))
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelModoExecucaoLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jRadioButtonModoPassoAPasso))
                    .addGroup(jPanelModoExecucaoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxEngine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelPasso4Layout = new javax.swing.GroupLayout(jPanelPasso4);
        jPanelPasso4.setLayout(jPanelPasso4Layout);
        jPanelPasso4Layout.setHorizontalGroup(
            jPanelPasso4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelPasso4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso4Layout.createSequentialGroup()
                        .addGroup(jPanelPasso4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addGap(0, 234, Short.MAX_VALUE))
                    .addGroup(jPanelPasso4Layout.createSequentialGroup()
                        .addGroup(jPanelPasso4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelModoExecucao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelSegundoConjunto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelPrimeiroConjunto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5))))
        );
        jPanelPasso4Layout.setVerticalGroup(
            jPanelPasso4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel13)
                .addGap(10, 10, 10)
                .addComponent(jLabel14)
                .addGap(10, 10, 10)
                .addComponent(jPanelPrimeiroConjunto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanelSegundoConjunto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanelModoExecucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanelPrincipal.add(jPanelPasso4, "Passo4");

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('c');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jButtonProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_avancar.png"))); // NOI18N
        jButtonProximo.setMnemonic('p');
        jButtonProximo.setText("Próximo");
        jButtonProximo.setEnabled(false);
        jButtonProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProximoActionPerformed(evt);
            }
        });

        jButtonAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_voltar.png"))); // NOI18N
        jButtonAnterior.setMnemonic('n');
        jButtonAnterior.setText("Anterior");
        jButtonAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnteriorActionPerformed(evt);
            }
        });

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setMnemonic('a');
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAnterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonProximo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancelar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelar)
                    .addComponent(jButtonProximo)
                    .addComponent(jButtonAnterior)
                    .addComponent(jButtonAjuda))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-713)/2, (screenSize.height-533)/2, 713, 533);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        fechar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnteriorActionPerformed
        
        etapaCriacao--;
        
        ativaAtualizacoesTree(true);
        
        switch(etapaCriacao){
            
            case 1:card.show(jPanelPrincipal,"Passo1");
                   jButtonAnterior.setVisible(false);
                   verificaTreeConjuntoHeuristico(treeModelConjuntosHeuristicos1);
                   break;
                
            case 2:card.show(jPanelPrincipal,"Passo2");                   
                   verificaTreeConjuntoHeuristico(treeModelConjuntosHeuristicos2);
                   break;
                
            case 3:card.show(jPanelPrincipal,"Passo3");
                   jButtonProximo.setText("Próximo"); 
                   jButtonProximo.setMnemonic('p');
                   verificaTreeSituacoesJogo();
                   break;            
        }        
    }//GEN-LAST:event_jButtonAnteriorActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        fechar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProximoActionPerformed
        avancarEtapa();
    }//GEN-LAST:event_jButtonProximoActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaParametrosPartida");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jTreeConjuntoHeuristicos1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicos1MouseClicked

        if (treeModelConjuntosHeuristicos1.verificaDuploClique(evt, ConjuntoHeuristico.class)){
            avancarEtapa();
        }
    }//GEN-LAST:event_jTreeConjuntoHeuristicos1MouseClicked

    private void jTreeConjuntoHeuristicos1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicos1ValueChanged
        verificaTreeConjuntoHeuristico(treeModelConjuntosHeuristicos1);
    }//GEN-LAST:event_jTreeConjuntoHeuristicos1ValueChanged

    private void jTreeConjuntoHeuristicos2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicos2MouseClicked
        
        if (treeModelConjuntosHeuristicos2.verificaDuploClique(evt, ConjuntoHeuristico.class)){
            avancarEtapa();
        }
    }//GEN-LAST:event_jTreeConjuntoHeuristicos2MouseClicked

    private void jTreeConjuntoHeuristicos2ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicos2ValueChanged
        verificaTreeConjuntoHeuristico(treeModelConjuntosHeuristicos2);
    }//GEN-LAST:event_jTreeConjuntoHeuristicos2ValueChanged

    private void jTreeSituacoesJogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeSituacoesJogoMouseClicked
        
        if (treeModelSituacoesJogo.verificaDuploClique(evt, SituacaoJogo.class)){
            avancarEtapa();
        }
    }//GEN-LAST:event_jTreeSituacoesJogoMouseClicked

    private void jTreeSituacoesJogoValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeSituacoesJogoValueChanged
        verificaTreeSituacoesJogo();
    }//GEN-LAST:event_jTreeSituacoesJogoValueChanged

    private void jComboBoxCorConjunto1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCorConjunto1ItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED){
            if (jComboBoxCorConjunto1.getSelectedIndex() == 0){
                jComboBoxCorConjunto2.setSelectedIndex(0);
            }else{
                jComboBoxCorConjunto2.setSelectedIndex(1);
            }
        }
    }//GEN-LAST:event_jComboBoxCorConjunto1ItemStateChanged

    private void jComboBoxCorConjunto2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCorConjunto2ItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED){
            if (jComboBoxCorConjunto2.getSelectedIndex() == 0){
                jComboBoxCorConjunto1.setSelectedIndex(0);
            }else{
                jComboBoxCorConjunto1.setSelectedIndex(1);
            }
        }
    }//GEN-LAST:event_jComboBoxCorConjunto2ItemStateChanged

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        ativaAtualizacoesTree(true);
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        ativaAtualizacoesTree(false);
    }//GEN-LAST:event_formWindowDeactivated

    private void jRadioButtonModoPassoAPassoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonModoPassoAPassoItemStateChanged
        
        if (jRadioButtonModoPassoAPasso.isSelected()){
            jLabelBoxEngine.setVisible(true);
            jComboBoxEngine.setVisible(true);            
        }
    }//GEN-LAST:event_jRadioButtonModoPassoAPassoItemStateChanged

    private void jRadioButtonModoAutomaticoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonModoAutomaticoItemStateChanged
        
        if (jRadioButtonModoAutomatico.isSelected()){
            jLabelBoxEngine.setVisible(false);
            jComboBoxEngine.setVisible(false);            
        }
    }//GEN-LAST:event_jRadioButtonModoAutomaticoItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupModoExecucao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JComboBox jComboBoxCorConjunto1;
    private javax.swing.JComboBox jComboBoxCorConjunto2;
    private javax.swing.JComboBox jComboBoxEngine;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBoxEngine;
    private javax.swing.JLabel jLabelDescricaoVantagem;
    private javax.swing.JPanel jPanelLayoutNull;
    private javax.swing.JPanel jPanelModoExecucao;
    private javax.swing.JPanel jPanelPasso1;
    private javax.swing.JPanel jPanelPasso2;
    private javax.swing.JPanel jPanelPasso3;
    private javax.swing.JPanel jPanelPasso4;
    private javax.swing.JPanel jPanelPrimeiroConjunto;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelSegundoConjunto;
    private javax.swing.JPanel jPanelTabuleiro;
    private javax.swing.JRadioButton jRadioButtonModoAutomatico;
    private javax.swing.JRadioButton jRadioButtonModoPassoAPasso;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSliderProfundidadeConjunto1;
    private javax.swing.JSlider jSliderProfundidadeConjunto2;
    private javax.swing.JTextField jTextFieldConjunto1;
    private javax.swing.JTextField jTextFieldConjunto2;
    private javax.swing.JTextField jTextFieldFEN;
    private javax.swing.JTree jTreeConjuntoHeuristicos1;
    private javax.swing.JTree jTreeConjuntoHeuristicos2;
    private javax.swing.JTree jTreeSituacoesJogo;
    // End of variables declaration//GEN-END:variables
}
