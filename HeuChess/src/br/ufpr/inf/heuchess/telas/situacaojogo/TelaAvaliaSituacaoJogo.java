package br.ufpr.inf.heuchess.telas.situacaojogo;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.Historico.Tipo;
import br.ufpr.inf.heuchess.competicaoheuristica.AvaliadorDHJOG;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.ConjuntoHeuristicoDAO;
import br.ufpr.inf.heuchess.persistencia.SituacaoJogoDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.iniciais.RenderTreeObjetos;
import br.ufpr.inf.utils.gui.*;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 12, 2012
 */
public class TelaAvaliaSituacaoJogo extends javax.swing.JFrame implements ModalFrameHierarchy {
    
    private CardLayout card;
    private int        etapaCriacao;    
    
    private TreeModelConjuntosHeuristicos treeModelConjuntosHeuristicos;
    private TreeModelSituacoesJogo        treeModelSituacoesJogo;
    
    private ModalFrameHierarchy acessoTelaAvaliaSituacaoJogo;    
    private DesenhaSituacaoJogo desenhaSituacaoJogo = new DesenhaSituacaoJogo(this);
        
    /**
     * Construtor chamado sem nenhum Conjunto Heurístico definido
     */
    public TelaAvaliaSituacaoJogo(ModalFrameHierarchy acessoTelaAvaliaSituacaoJogo) {
        
        try{
            inicializacaoBasica(acessoTelaAvaliaSituacaoJogo);
        }catch(Exception e){
            acessoTelaAvaliaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(acessoTelaAvaliaSituacaoJogo.getFrame(),"Erro ao carregar dados!\n" + e.getMessage());
            dispose();
            return;
        }
        
        acessoTelaAvaliaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        ModalFrameUtil.showAsModalDontBlock(this); 
    }

    /**
     * Construtor chamado com um ElementoLista já escolhido
     */
    public TelaAvaliaSituacaoJogo(ModalFrameHierarchy acessoTelaAvaliaSituacaoJogo, Class classe, long id) {

        try{
            inicializacaoBasica(acessoTelaAvaliaSituacaoJogo);
        }catch(Exception e){
            acessoTelaAvaliaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(acessoTelaAvaliaSituacaoJogo.getFrame(),"Erro ao carregar dados!\n" + e.getMessage());
            dispose();
            return;
        }
        
        if (classe == ConjuntoHeuristico.class){
            
            if (treeModelConjuntosHeuristicos.selecionaTreeNode(classe, id)){                
        
                etapaCriacao = 2;
        
                card.show(jPanelPrincipal,"Passo2");
        
                jButtonAnterior.setVisible(true);
                jButtonProximo.setText("Avaliar"); 
                jButtonProximo.setMnemonic('a');
                jButtonProximo.setEnabled(false);
            }
        }else
            if (classe == SituacaoJogo.class){
                
                treeModelSituacoesJogo.selecionaTreeNode(classe, id);
                                
            }else{
                acessoTelaAvaliaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(acessoTelaAvaliaSituacaoJogo.getFrame(),"Tipo de elemento da Lista não suportado por este Construtor [" + classe.getName() + "]");
                dispose();
                return;
            }
                
        acessoTelaAvaliaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        ModalFrameUtil.showAsModalDontBlock(this); 
    }
    
    private void inicializacaoBasica(ModalFrameHierarchy acessoTelaAvaliaSituacaoJogo) throws Exception {
        
        etapaCriacao = 1;
        
        this.acessoTelaAvaliaSituacaoJogo = acessoTelaAvaliaSituacaoJogo;        
        
        desenhaSituacaoJogo.setEditavel(false);
        
        initComponents();
        
        treeModelConjuntosHeuristicos = new TreeModelConjuntosHeuristicos(jTreeConjuntoHeuristicos);
        treeModelSituacoesJogo        = new TreeModelSituacoesJogo(jTreeSituacoesJogo);
        
        treeModelConjuntosHeuristicos.forceUpdate();
        treeModelConjuntosHeuristicos.expandMeusConjuntos();
        
        treeModelSituacoesJogo.forceUpdate();
        treeModelSituacoesJogo.expandPadrao();        
        treeModelSituacoesJogo.expandMinhasSituacoes();        
        
        desenhaSituacaoJogo.setSize(jPanelTabuleiro.getWidth(),jPanelTabuleiro.getHeight());
        
        card = (CardLayout) jPanelPrincipal.getLayout();                
        
        jButtonAnterior.setVisible(false);
        jButtonProximo.setEnabled(false);
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaAvaliaSituacaoJogo;
    }
    
    private void fechar(){
        dispose();           
    }        
    
    private void verificaTreeConjuntoHeuristico(){
        
        if (treeModelConjuntosHeuristicos.recuperaElementoLista(ConjuntoHeuristico.class) != null) {
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
        
        } else {
            jButtonProximo.setEnabled(false);
            jPanelTabuleiro.setVisible(false);
            jTextFieldFEN.setText("");
            jLabelDescricaoVantagem.setText("");
        }
    }
    
    private void avancarEtapa() {
        
        etapaCriacao++;
        
        ativaAtualizacoesTree(true);
        
        switch (etapaCriacao) {
            
            case 2:
                card.show(jPanelPrincipal, "Passo2");
                jButtonAnterior.setVisible(true);
                jButtonProximo.setText("Avaliar");
                jButtonProximo.setMnemonic('a');
                verificaTreeSituacoesJogo();
                break;
                
            case 3:
                mostraAvaliacao();
                break;
        }
    }
    
    private void mostraAvaliacao() {
        
        ElementoLista elementoConjuntoHeuristico = treeModelConjuntosHeuristicos.recuperaElementoLista(ConjuntoHeuristico.class);

        if (elementoConjuntoHeuristico == null) {
            etapaCriacao--;            
            return;
        }

        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        ConjuntoHeuristico conjuntoHeuristico;
        
        try{
            conjuntoHeuristico = ConjuntoHeuristicoDAO.busca(elementoConjuntoHeuristico.getId());
        }catch(Exception e){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(this,"Erro ao carregar Conjunto Heurístico do Banco!\n" + e.getMessage());
            etapaCriacao--;
            return;
        }        
        
        card.show(jPanelPrincipal, "Passo3");
        jButtonProximo.setVisible(false);
        jButtonCancelar.setText("Fechar");
        jButtonCancelar.setMnemonic('F');            
        jButtonCancelar.setIcon(new ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        
        try{
            AvaliadorDHJOG analisePosicao = new AvaliadorDHJOG(conjuntoHeuristico,null);
            
            jTextPaneAvaliacao.setText(null);
            jLabelAvaliacao.setText("Avaliação da Situação de Jogo - \"" + jTextFieldFEN.getText() + "\"");
            
            boolean corBranca = (jComboBoxCorAvaliacao.getSelectedIndex() == 0 ? true : false);
                        
            analisePosicao.avalia(new Tabuleiro(jTextFieldFEN.getText()), jTextPaneAvaliacao, corBranca);    
            
            ///////////////////////////////////////////////
            // Registra Histórico de Uso dos Componentes //
            ///////////////////////////////////////////////
            
            Historico.registraComponenteUsado(Tipo.USOU_COMPONENTE_AVALIACAO, conjuntoHeuristico, corBranca);
            
            ElementoLista elementoSituacaoJogo = treeModelSituacoesJogo.recuperaElementoLista(SituacaoJogo.class);

            if (elementoSituacaoJogo.getId() != TreeModelSituacoesJogo.COD_TABULEIRO_PADRAO) {

                SituacaoJogo situacaoJogo = SituacaoJogoDAO.busca(elementoSituacaoJogo.getId());

                Historico.registraComponenteUsado(Tipo.USOU_COMPONENTE_AVALIACAO, situacaoJogo, false);
            }
            
            ConexaoDBHeuChess.commit();
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
        }catch(Exception e){
            HeuChess.desfazTransacao(e);
            
            jTextPaneAvaliacao.setText(jTextPaneAvaliacao.getText() + "\n\nExceção:\n\n" + e.getMessage());
            
            jTextPaneAvaliacao.setText(jTextPaneAvaliacao.getText() + "\n\nPilha de Execucação:\n");
            for (StackTraceElement track : e.getStackTrace()){
                jTextPaneAvaliacao.setText(jTextPaneAvaliacao.getText() + "\n" + track.toString());
            }
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this,"Erro ao realizar a Avaliação Heurística da Situação de Jogo!");
        }
    }
    
    private void ativaAtualizacoesTree(boolean ativar) {
        
        if (ativar) {
            
            switch(etapaCriacao){
                
                case 1:
                    if (treeModelConjuntosHeuristicos != null && !treeModelConjuntosHeuristicos.isExecutando()) {
                        treeModelConjuntosHeuristicos.start();
                    }    
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
                    
                case 2:
                    if (treeModelConjuntosHeuristicos != null && treeModelConjuntosHeuristicos.isExecutando()) {
                        treeModelConjuntosHeuristicos.stop();
                    }    
                    if (treeModelSituacoesJogo != null && !treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.start();
                    }
                    break;
                    
                case 3:
                    if (treeModelConjuntosHeuristicos != null && treeModelConjuntosHeuristicos.isExecutando()) {
                        treeModelConjuntosHeuristicos.stop();
                    }    
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
            }
            
        } else {
            
            if (treeModelConjuntosHeuristicos != null && treeModelConjuntosHeuristicos.isExecutando()) {
                treeModelConjuntosHeuristicos.stop();
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

        jPanelPrincipal = new javax.swing.JPanel();
        jPanelPasso1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTreeConjuntoHeuristicos = new javax.swing.JTree();
        jPanelPasso2 = new javax.swing.JPanel();
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
        jLabel2 = new javax.swing.JLabel();
        jComboBoxCorAvaliacao = new javax.swing.JComboBox();
        jPanelPasso3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabelAvaliacao = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneAvaliacao = new javax.swing.JTextPane();
        jButtonCancelar = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Avalia Situação de Jogo");
        setIconImage(new ImageIcon(getClass().getResource("/icones/avaliar-heuristica.png")).getImage());
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

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Passo 1 de 3");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Escolha o Conjunto Heurístico");

        jTreeConjuntoHeuristicos.setShowsRootHandles(true);
        jTreeConjuntoHeuristicos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeConjuntoHeuristicos.setCellRenderer(new RenderTreeObjetos(true));
        jTreeConjuntoHeuristicos.setToggleClickCount(1);
        jTreeConjuntoHeuristicos.setScrollsOnExpand(true);
        jTreeConjuntoHeuristicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeConjuntoHeuristicosMouseClicked(evt);
            }
        });
        jTreeConjuntoHeuristicos.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeConjuntoHeuristicosValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTreeConjuntoHeuristicos);

        javax.swing.GroupLayout jPanelPasso1Layout = new javax.swing.GroupLayout(jPanelPasso1);
        jPanelPasso1.setLayout(jPanelPasso1Layout);
        jPanelPasso1Layout.setHorizontalGroup(
            jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanelPasso1Layout.createSequentialGroup()
                        .addGroup(jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel23))
                        .addGap(0, 512, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPasso1Layout.setVerticalGroup(
            jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelPasso1, "Passo1");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Passo 2 de 3");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Defina a Situação de Jogo que será Avaliada");

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

        jLabel2.setText("Avaliar vantagem das:");

        jComboBoxCorAvaliacao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BRANCAS", "PRETAS" }));
        jComboBoxCorAvaliacao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelPasso2Layout = new javax.swing.GroupLayout(jPanelPasso2);
        jPanelPasso2.setLayout(jPanelPasso2Layout);
        jPanelPasso2Layout.setHorizontalGroup(
            jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel12)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jComboBoxCorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldFEN))))
                .addContainerGap())
        );
        jPanelPasso2Layout.setVerticalGroup(
            jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 26, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jComboBoxCorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldFEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(5, 5, 5)
                        .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))))
        );

        jPanelPrincipal.add(jPanelPasso2, "Passo2");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Passo 3 de 3");

        jLabelAvaliacao.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelAvaliacao.setText("Avaliação da Situação de Jogo -");

        jTextPaneAvaliacao.setEditable(false);
        jScrollPane1.setViewportView(jTextPaneAvaliacao);

        javax.swing.GroupLayout jPanelPasso3Layout = new javax.swing.GroupLayout(jPanelPasso3);
        jPanelPasso3.setLayout(jPanelPasso3Layout);
        jPanelPasso3Layout.setHorizontalGroup(
            jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelPasso3Layout.createSequentialGroup()
                        .addGroup(jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabelAvaliacao))
                        .addGap(0, 499, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPasso3Layout.setVerticalGroup(
            jPanelPasso3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelAvaliacao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelPasso3, "Passo3");

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
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
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
        setBounds((screenSize.width-730)/2, (screenSize.height-528)/2, 730, 528);
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
                   jButtonProximo.setText("Próximo"); 
                   jButtonProximo.setMnemonic('p');
                   
                   verificaTreeConjuntoHeuristico();
                   break;
                
            case 2:card.show(jPanelPrincipal,"Passo2");
            
                   jButtonProximo.setVisible(true);
                   jButtonProximo.setText("Avaliar"); 
                   jButtonProximo.setMnemonic('a');
                   
                   jButtonCancelar.setText("Cancelar");
                   jButtonCancelar.setMnemonic('c');            
                   jButtonCancelar.setIcon(new ImageIcon(getClass().getResource("/icones/icone_cancelar.png")));
                   
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
        HeuChess.ajuda.abre(this, "TelaAvaliaSituacaoJogo");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jTreeSituacoesJogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeSituacoesJogoMouseClicked
        
        if (treeModelSituacoesJogo.verificaDuploClique(evt, SituacaoJogo.class)){
            avancarEtapa();
        }
    }//GEN-LAST:event_jTreeSituacoesJogoMouseClicked

    private void jTreeSituacoesJogoValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeSituacoesJogoValueChanged
        verificaTreeSituacoesJogo();
    }//GEN-LAST:event_jTreeSituacoesJogoValueChanged

    private void jTreeConjuntoHeuristicosValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicosValueChanged
        verificaTreeConjuntoHeuristico();
    }//GEN-LAST:event_jTreeConjuntoHeuristicosValueChanged

    private void jTreeConjuntoHeuristicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicosMouseClicked
        
        if (treeModelConjuntosHeuristicos.verificaDuploClique(evt, ConjuntoHeuristico.class)){
            avancarEtapa();
        }
    }//GEN-LAST:event_jTreeConjuntoHeuristicosMouseClicked

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        ativaAtualizacoesTree(true);
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        ativaAtualizacoesTree(false);
    }//GEN-LAST:event_formWindowDeactivated
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JComboBox jComboBoxCorAvaliacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelAvaliacao;
    private javax.swing.JLabel jLabelDescricaoVantagem;
    private javax.swing.JPanel jPanelLayoutNull;
    private javax.swing.JPanel jPanelPasso1;
    private javax.swing.JPanel jPanelPasso2;
    private javax.swing.JPanel jPanelPasso3;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelTabuleiro;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextFieldFEN;
    private javax.swing.JTextPane jTextPaneAvaliacao;
    private javax.swing.JTree jTreeConjuntoHeuristicos;
    private javax.swing.JTree jTreeSituacoesJogo;
    // End of variables declaration//GEN-END:variables
}
