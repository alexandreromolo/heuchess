package br.ufpr.inf.heuchess.telas.situacaojogo;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.SituacaoJogoDAO;
import br.ufpr.inf.heuchess.persistencia.TurmaDAO;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Peca;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.editorheuristica.AcessoTelaAnotacao;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.heuchess.telas.iniciais.TelaPrincipal;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 30 de Julho de 2006, 17:02
 */
public class TelaSituacaoJogo extends javax.swing.JFrame implements AcessoTelaAnotacao, AcessoTelaUsuario, PropertyChangeListener {
    
    public  SituacaoJogo situacaoJogo;
    private SituacaoJogo situacaoJogoOriginal;
        
    private boolean nova;
    private boolean alteracao;
    private boolean criadaNovaBanco;
    
    private boolean podeAlterar;
    private boolean podeAnotar;
    
    private ModalFrameHierarchy acessoTelaSituacaoJogo;
    
    private DesenhaSituacaoJogo desenhaSituacaoJogo = new DesenhaSituacaoJogo(this);
    
    /***
     * Construtor chamado quando se está criando uma nova Situação de Jogo
     */
    public TelaSituacaoJogo(final ModalFrameHierarchy acessoTelaSituacaoJogo) {
        
        setEnabled(false);
        
        this.acessoTelaSituacaoJogo = acessoTelaSituacaoJogo;
        
        try {
            
            nova = true;
            podeAlterar = true;
            podeAnotar  = true;
            
            situacaoJogo = new SituacaoJogo("",
                                            HeuChess.usuario.getId(),
                                            Tabuleiro.TABULEIRO_MINIMO,
                                            SituacaoJogoDAO.tiposSituacaoJogo.get(3));
            
            desenhaSituacaoJogo.configura(situacaoJogo.getFEN());            
            desenhaSituacaoJogo.apagaPecaSemEvento(0,4); // Rei Branco
            desenhaSituacaoJogo.apagaPecaSemEvento(7,4); // Rei Preto
            desenhaSituacaoJogo.atualizaFEN(false);
            
            montaInterface();
            
        } catch (Exception ex) {            
            
            if (acessoTelaSituacaoJogo instanceof TelaPrincipal){
                acessoTelaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            HeuChess.registraExcecao(ex);
            
            UtilsGUI.dialogoErro(acessoTelaSituacaoJogo.getFrame(), "Erro ao Criar nova situação de Jogo\n" + ex.getMessage());
            dispose();
        }
    }
    
    /**
     * Construtor chamado passando uma situação de jogo já existente
     */
    public TelaSituacaoJogo(final ModalFrameHierarchy acessoTelaSituacaoJogo, SituacaoJogo situacao){
        
        setEnabled(false);
                
        this.acessoTelaSituacaoJogo = acessoTelaSituacaoJogo;
        
        try {
            
            situacaoJogoOriginal = situacao;
            situacaoJogo = situacaoJogoOriginal.geraClone();
            
            desenhaSituacaoJogo.configura(situacaoJogo.getFEN());
            
            // Pode editar os dados de um Situação de Jogo caso seja Administrador, o próprio autor, 
            // o coordenador de uma turma dele, ou um companheiro de turma de acordo com as permissões da turma.
        
            if ((HeuChess.usuario.getId()   == situacaoJogo.getIdAutor()) ||
                (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR)){
            
                podeAlterar = true;
                podeAnotar  = true;
            
            }else            
                if (UsuarioDAO.coordenoTurma(HeuChess.usuario,situacaoJogo.getIdAutor()) != -1){
                
                    podeAlterar = true;
                    podeAnotar  = true;
                
                }else{
                    ArrayList<Integer> permissoes = TurmaDAO.listaPermissoes(HeuChess.usuario,situacaoJogo.getIdAutor());
                    
                    for (Integer inteiro : permissoes){
                        if (Permissao.ALTERAR.existe(inteiro.intValue())){
                           podeAlterar = true;
                           break;
                        }
                    }
                    
                    for (Integer inteiro : permissoes){
                        if (Permissao.ANOTAR.existe(inteiro.intValue())){
                           podeAnotar = true;
                           break;
                        }
                    }
                }
            
            montaInterface();
        
        } catch (Exception e) {
            
            if (acessoTelaSituacaoJogo instanceof TelaPrincipal){
                acessoTelaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(acessoTelaSituacaoJogo.getFrame(), "Erro ao abrir Situação de Jogo\n" + e.getMessage());
            dispose();
        }
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return null;
    }
    
    private void montaInterface() throws Exception {
        
        initComponents();   
        
        jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(situacaoJogo.getIdAutor())));
                
        desenhaSituacaoJogo.setSize(jPanelTabuleiro.getWidth(),jPanelTabuleiro.getHeight());        
        desenhaSituacaoJogo.setEditavel(true);
        desenhaSituacaoJogo.addPropertyChangeListener(this);
                
        setTitle("Situação de Jogo - " + jTextFieldNomeSituacaoJogo.getText());        
        
        atualizaInterface(nova ? false : true);
        
        Anotacoes.atualizaQuantidadeAnotacoes(this);
        
        alteracao = false;
        
        if (!podeAlterar){
            jTextFieldNomeSituacaoJogo.setEditable(false);
            jRadioButtonBrancas.setEnabled(false);
            jRadioButtonPretas.setEnabled(false);
            jCheckBoxRoqueMaiorBrancas.setEnabled(false);
            jCheckBoxRoqueMenorBrancas.setEnabled(false);
            jCheckBoxRoqueMaiorPretas.setEnabled(false);
            jCheckBoxRoqueMenorPretas.setEnabled(false);
            jComboBoxCasaEnPassant.setEnabled(false);
            jSpinnerQuantidadeJogadas.setEnabled(false);
            jSpinnerQuantidadeMovimentos.setEnabled(false);
            jSliderVantagem.setEnabled(false);
            
            jToggleButtonPeaoBranco.setEnabled(false);
            jToggleButtonTorreBranca.setEnabled(false);
            jToggleButtonCavaloBranco.setEnabled(false);
            jToggleButtonBispoBranco.setEnabled(false);
            jToggleButtonDamaBranca.setEnabled(false);
            jToggleButtonReiBranco.setEnabled(false);
            jToggleButtonPeaoPreto.setEnabled(false);
            jToggleButtonTorrePreta.setEnabled(false);
            jToggleButtonCavaloPreto.setEnabled(false);
            jToggleButtonBispoPreto.setEnabled(false);
            jToggleButtonDamaPreta.setEnabled(false);
            jToggleButtonReiPreto.setEnabled(false);
            
            jToggleButtonBorracha.setEnabled(false);
            
            jButtonExcluirAnotacao.setVisible(false);
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
            
            jButtonEntradaFEN.setVisible(false);
        }
        
        if (!podeAnotar){

            jButtonAdicionarAnotacao.setVisible(false);            
            
            if (situacaoJogo.getAnotacoes().isEmpty()){
                jTabbedPanePrincipal.remove(jPanelAnotacoes);
            }
        }
        
        if (!nova){            
            Historico.registraComponenteAberto(situacaoJogo);
            ConexaoDBHeuChess.commit();
        }
        
        //////////////////////
        // Minimiza Janelas //
        //////////////////////
        
        if (acessoTelaSituacaoJogo instanceof TelaPrincipal){
            acessoTelaSituacaoJogo.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
        HeuChess.telaPrincipal.minimizaTodasJanelas();
        HeuChess.telaPrincipal.telasSituacaoJogo.add(this);
                
        ///////////////////
        // Mostra Janela //
        ///////////////////
        
        setVisible(true);
        setEnabled(true);
        toFront();
        
        jTextFieldNomeSituacaoJogo.requestFocus();
    }
    
    private void atualizaInterface(boolean definirProximoJogador) {
        
        jTextFieldFEN.setText(desenhaSituacaoJogo.getFEN());

        if (definirProximoJogador){
            jRadioButtonBrancas.setSelected(desenhaSituacaoJogo.isJogadorBrancoAtivo());
            jRadioButtonPretas.setSelected(!desenhaSituacaoJogo.isJogadorBrancoAtivo());
        }

        jCheckBoxRoqueMaiorBrancas.setSelected(desenhaSituacaoJogo.isPossivelRoqueMaiorBrancas());
        jCheckBoxRoqueMenorBrancas.setSelected(desenhaSituacaoJogo.isPossivelRoqueMenorBrancas());
        jCheckBoxRoqueMaiorPretas.setSelected(desenhaSituacaoJogo.isPossivelRoqueMaiorPretas());
        jCheckBoxRoqueMenorPretas.setSelected(desenhaSituacaoJogo.isPossivelRoqueMenorPretas());

        Casa casaEnPassant = desenhaSituacaoJogo.getCasaEnPassant();
        if (casaEnPassant != null) {
            jComboBoxCasaEnPassant.setSelectedItem(casaEnPassant.getFEN());
        }

        jSpinnerQuantidadeMovimentos.setValue(desenhaSituacaoJogo.getQuantidadeMovimentos());
        jSpinnerQuantidadeJogadas.setValue(desenhaSituacaoJogo.getQuantidadeJogadas());
    }
    
    private void confirmaCancelar(){
        
        if (podeAlterar || podeAnotar) {

            if (alteracao || criadaNovaBanco) {
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?", 
                                                                 "Confirmação Cancelamento");
                
                if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                    return;
                }

                if (criadaNovaBanco) {
                    try {
                        setCursor(new Cursor(Cursor.WAIT_CURSOR));

                        SituacaoJogoDAO.apaga(situacaoJogo.getId());
                        ConexaoDBHeuChess.commit();

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        
                    } catch (Exception e) {
                        HeuChess.desfazTransacao(e);

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        UtilsGUI.dialogoErro(this, "Erro ao tentar desfazer alterações no Banco de Dados\nOperação Cancelada!");
                    }
                }
            }
        }
        
        dispose();        
        HeuChess.telaPrincipal.desvincultarTrazerOutraTelaFrente(this);
    }
    
    @Override
    public JList getJListAnotacoes() {
        return jListAnotacoes;
    }

    @Override
    public JLabel getJLabelTotalAnotacoes() {
        return jLabelTotalAnotacoes;
    }

    @Override
    public JButton getJButtonAbrirAnotacao() {
        return jButtonAbrirAnotacao;
    }

    @Override
    public JButton getJButtonExcluirAnotacao() {
        return jButtonExcluirAnotacao;
    }

    @Override
    public Componente getComponente() {
        return situacaoJogo;
    }

    @Override
    public boolean podeAlterar() {
        return podeAlterar;
    }
    
    @Override
    public void atualizaVersaoDataUltimaModificacao() {
        jTextFieldVersao.setText(String.valueOf(situacaoJogo.getVersao()));
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(situacaoJogo.getDataUltimaModificacao()));
    }
    
    @Override
    public void fechandoTelaAnotacao(boolean sucesso) {
        
    }    
    
    public void adicionaNovaAnotacao(){
        
        if (nova && !criadaNovaBanco){
            
            if (!salvarEntrada("\n\nÉ preciso tornar a Situação de Jogo válida antes de criar uma Anotação para ela!")){
                return;
            }
            
            try{
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                SituacaoJogoDAO.adiciona(situacaoJogo);
                
                ConexaoDBHeuChess.commit();
            
                criadaNovaBanco = true;            
                alteracao       = false;
                jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(situacaoJogo.getDataCriacao()));
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
            }catch(Exception e){
                HeuChess.desfazTransacao(e);                
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar criar a Situação de Jogo no Banco de Dados\nOperação Cancelada!");
                return;
            }
        }        
    
        Anotacoes.novaAnotacao(this);  
    }
    
    private boolean salvarEntrada(String complemento){
       
        String nome = jTextFieldNomeSituacaoJogo.getText();
        
        if (nome == null || nome.trim().length() == 0){
            UtilsGUI.dialogoErro(this, "O Nome da Situação de Jogo não esta preenchido.\n" +
                                       "Uma Situação de Jogo precisa ter um nome definido para poder ser salva!"+complemento);
            return false;            
        }    
        
        nome = UtilsString.preparaStringParaBD(nome, true, Formato.TUDO_MAIUSCULO);
        
        String erro = DHJOG.validaNomeComponenteGeral(nome);

        if (erro != null){    
            
             UtilsGUI.dialogoErro(this,erro+complemento);
             if (!nova){
                 jTextFieldNomeSituacaoJogo.setText(situacaoJogo.getNome());
             }
             
             jTextFieldNomeSituacaoJogo.selectAll();
             jTextFieldNomeSituacaoJogo.requestFocus();
             return false;            
        }
        
        try {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            long idAchado = SituacaoJogoDAO.existeNome(HeuChess.usuario, nome);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            if (idAchado != -1 && situacaoJogo.getId() != idAchado) {

                UtilsGUI.dialogoErro(this, "Você já possui uma Situação de Jogo com este nome!.\n" +
                                           "Escolha outro." + complemento);

                jTextFieldNomeSituacaoJogo.requestFocus();
                jTextFieldNomeSituacaoJogo.selectAll();
                return false;
            }
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao tentar localizar Nome da Situação de Jogo no Banco de Dados!");
            return false;
        }
        
        if (desenhaSituacaoJogo.contaPecasJogador(true) == 0 || desenhaSituacaoJogo.possuiRei(true) == false){
            
            UtilsGUI.dialogoErro(this, "O jogador com as Brancas tem que ter pelo menos o Rei");
            return false;        
        }
        
        if (desenhaSituacaoJogo.contaPecasJogador(false) == 0 || desenhaSituacaoJogo.possuiRei(false) == false){
                
            UtilsGUI.dialogoErro(this, "O jogador com as Pretas tem que ter pelo menos o Rei");
            return false;        
        }
        
        if (!jRadioButtonBrancas.isSelected() && !jRadioButtonPretas.isSelected()){
                
            UtilsGUI.dialogoErro(this, "Não foi definido qual será o próximo a jogar, Brancas ou Pretas?");
            return false;        
        }
        
        try {
            situacaoJogo.setFEN(desenhaSituacaoJogo.getFEN());
            situacaoJogo.setNome(nome);
            situacaoJogo.setTipo(SituacaoJogoDAO.tiposSituacaoJogo.get(jSliderVantagem.getValue()));
            
        } catch (Exception ex) {
            UtilsGUI.dialogoErro(this, ex.getMessage());
            return false;
        }
        
        return true;
    }
    
    public void fechandoTelaEntradaFEN(Tabuleiro tabuleiro){
    
        if (tabuleiro != null && podeAlterar) {
            
            desenhaSituacaoJogo.configura(tabuleiro);
            
            atualizaInterface(true);
            
            alteracao = true;
        }
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null){
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(usuario.getNome()));
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (evt.getPropertyName().equalsIgnoreCase("peca_apagada")){
            if (HeuChess.somAtivado){
                HeuChess.somApagar.play();
            }
        }
        
        if (evt.getPropertyName().equalsIgnoreCase("peca_incluida")) {
            if (HeuChess.somAtivado) {
                HeuChess.somPecaColocada.play();
            }
        }
        
        jTextFieldFEN.setText(desenhaSituacaoJogo.getFEN());
        
        alteracao = true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupJogadorAtivo = new javax.swing.ButtonGroup();
        buttonGroupPecas = new javax.swing.ButtonGroup();
        jPanelBotoesInferior = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jPanelCentralizaBotaoEntradaFEN = new javax.swing.JPanel();
        jButtonEntradaFEN = new javax.swing.JButton();
        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelDadosPrincipais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNomeSituacaoJogo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jButtonDadosAutor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldVersao = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jPanelDadosTabuleiro = new javax.swing.JPanel();
        jToolBarPecas = new javax.swing.JToolBar();
        jToggleButtonPeaoBranco = new javax.swing.JToggleButton();
        jToggleButtonTorreBranca = new javax.swing.JToggleButton();
        jToggleButtonCavaloBranco = new javax.swing.JToggleButton();
        jToggleButtonBispoBranco = new javax.swing.JToggleButton();
        jToggleButtonDamaBranca = new javax.swing.JToggleButton();
        jToggleButtonReiBranco = new javax.swing.JToggleButton();
        jToggleButtonPeaoPreto = new javax.swing.JToggleButton();
        jToggleButtonTorrePreta = new javax.swing.JToggleButton();
        jToggleButtonCavaloPreto = new javax.swing.JToggleButton();
        jToggleButtonBispoPreto = new javax.swing.JToggleButton();
        jToggleButtonDamaPreta = new javax.swing.JToggleButton();
        jToggleButtonReiPreto = new javax.swing.JToggleButton();
        jPanelComplementoFEN = new javax.swing.JPanel();
        jRadioButtonBrancas = new javax.swing.JRadioButton();
        jRadioButtonPretas = new javax.swing.JRadioButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jComboBoxCasaEnPassant = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jSpinnerQuantidadeMovimentos = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jSpinnerQuantidadeJogadas = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxRoqueMaiorBrancas = new javax.swing.JCheckBox();
        jCheckBoxRoqueMaiorPretas = new javax.swing.JCheckBox();
        jCheckBoxRoqueMenorBrancas = new javax.swing.JCheckBox();
        jCheckBoxRoqueMenorPretas = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jSliderVantagem = new javax.swing.JSlider();
        jLabelDescricaoVantagem = new javax.swing.JLabel();
        jPanelLayoutNull = new javax.swing.JPanel();
        jPanelTabuleiro = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldFEN = new javax.swing.JTextField();
        jToolBarApagar = new javax.swing.JToolBar();
        jToggleButtonBorracha = new javax.swing.JToggleButton();
        jPanelAnotacoes = new javax.swing.JPanel();
        jPanelBotoesAnotacao = new javax.swing.JPanel();
        jButtonAbrirAnotacao = new javax.swing.JButton();
        jButtonAdicionarAnotacao = new javax.swing.JButton();
        jButtonExcluirAnotacao = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListAnotacoes = new JList(new br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes(situacaoJogo.getAnotacoes()));
        jLabelTituloListaAnotacoes = new javax.swing.JLabel();
        jLabelTotalAnotacoes = new javax.swing.JLabel();
        jPanelCodigoGerado = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaCodigoGerado = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Situação de Jogo - ");
        setIconImage(new ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
            }
        });

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.setMaximumSize(new java.awt.Dimension(101, 25));
        jButtonCancelar.setMinimumSize(new java.awt.Dimension(101, 25));
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jPanelCentralizaBotaoEntradaFEN.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jButtonEntradaFEN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png"))); // NOI18N
        jButtonEntradaFEN.setMnemonic('e');
        jButtonEntradaFEN.setText("Entrar com FEN");
        jButtonEntradaFEN.setToolTipText("Configura a Situação de Jogo a partir de um FEN");
        jButtonEntradaFEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEntradaFENActionPerformed(evt);
            }
        });
        jPanelCentralizaBotaoEntradaFEN.add(jButtonEntradaFEN);

        javax.swing.GroupLayout jPanelBotoesInferiorLayout = new javax.swing.GroupLayout(jPanelBotoesInferior);
        jPanelBotoesInferior.setLayout(jPanelBotoesInferiorLayout);
        jPanelBotoesInferiorLayout.setHorizontalGroup(
            jPanelBotoesInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBotoesInferiorLayout.createSequentialGroup()
                .addComponent(jButtonAjuda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelCentralizaBotaoEntradaFEN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelBotoesInferiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jPanelBotoesInferiorLayout.setVerticalGroup(
            jPanelBotoesInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBotoesInferiorLayout.createSequentialGroup()
                .addGroup(jPanelBotoesInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAjuda)
                    .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonConfirmar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanelCentralizaBotaoEntradaFEN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanelBotoesInferiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jTabbedPanePrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPanePrincipalStateChanged(evt);
            }
        });

        jPanelDadosPrincipais.setMaximumSize(new java.awt.Dimension(700, 500));
        jPanelDadosPrincipais.setMinimumSize(new java.awt.Dimension(698, 498));

        jLabel1.setText("Nome");

        jTextFieldNomeSituacaoJogo.setDocument(new DocumentMasked(DHJOG.CARACTERES_VALIDOS,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNomeSituacaoJogo.setText(situacaoJogo.getNome());
        jTextFieldNomeSituacaoJogo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeSituacaoJogoKeyReleased(evt);
            }
        });

        jLabel2.setText("Autor");

        jTextFieldNomeAutor.setEditable(false);

        jButtonDadosAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_dados_autor.png"))); // NOI18N
        jButtonDadosAutor.setText("Dados do Autor");
        jButtonDadosAutor.setToolTipText("Mostra mais informações sobre o Autor");
        jButtonDadosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDadosAutorActionPerformed(evt);
            }
        });

        jLabel3.setText("Versão");

        jTextFieldVersao.setEditable(false);
        jTextFieldVersao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldVersao.setText(String.valueOf(situacaoJogo.getVersao()));

        jLabel4.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(situacaoJogo.getDataCriacao()));

        jLabel5.setText("Modificação");

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(situacaoJogo.getDataUltimaModificacao()));

        jPanelDadosTabuleiro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jToolBarPecas.setFloatable(false);

        buttonGroupPecas.add(jToggleButtonPeaoBranco);
        jToggleButtonPeaoBranco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/p21o.PNG"))); // NOI18N
        jToggleButtonPeaoBranco.setToolTipText("Adiciona Peão Branco");
        jToggleButtonPeaoBranco.setFocusable(false);
        jToggleButtonPeaoBranco.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonPeaoBranco.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonPeaoBranco.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonPeaoBranco.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonPeaoBranco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPeaoBrancoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonPeaoBranco);

        buttonGroupPecas.add(jToggleButtonTorreBranca);
        jToggleButtonTorreBranca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/r21o.PNG"))); // NOI18N
        jToggleButtonTorreBranca.setToolTipText("Adiciona Torre Branca");
        jToggleButtonTorreBranca.setFocusable(false);
        jToggleButtonTorreBranca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonTorreBranca.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonTorreBranca.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonTorreBranca.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonTorreBranca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTorreBrancaActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonTorreBranca);

        buttonGroupPecas.add(jToggleButtonCavaloBranco);
        jToggleButtonCavaloBranco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/n21o.PNG"))); // NOI18N
        jToggleButtonCavaloBranco.setToolTipText("Adiciona Cavalo Branco");
        jToggleButtonCavaloBranco.setFocusable(false);
        jToggleButtonCavaloBranco.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonCavaloBranco.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonCavaloBranco.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonCavaloBranco.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonCavaloBranco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCavaloBrancoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonCavaloBranco);

        buttonGroupPecas.add(jToggleButtonBispoBranco);
        jToggleButtonBispoBranco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/b21o.PNG"))); // NOI18N
        jToggleButtonBispoBranco.setToolTipText("Adiciona Bispo Branco");
        jToggleButtonBispoBranco.setFocusable(false);
        jToggleButtonBispoBranco.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonBispoBranco.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBispoBranco.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBispoBranco.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonBispoBranco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBispoBrancoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonBispoBranco);

        buttonGroupPecas.add(jToggleButtonDamaBranca);
        jToggleButtonDamaBranca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/q21o.PNG"))); // NOI18N
        jToggleButtonDamaBranca.setToolTipText("Adiciona Dama Branca");
        jToggleButtonDamaBranca.setFocusable(false);
        jToggleButtonDamaBranca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonDamaBranca.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonDamaBranca.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonDamaBranca.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonDamaBranca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonDamaBrancaActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonDamaBranca);

        buttonGroupPecas.add(jToggleButtonReiBranco);
        jToggleButtonReiBranco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/k21o.PNG"))); // NOI18N
        jToggleButtonReiBranco.setToolTipText("Adiciona Rei Branco");
        jToggleButtonReiBranco.setFocusable(false);
        jToggleButtonReiBranco.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonReiBranco.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonReiBranco.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonReiBranco.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonReiBranco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonReiBrancoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonReiBranco);

        buttonGroupPecas.add(jToggleButtonPeaoPreto);
        jToggleButtonPeaoPreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/p21s.PNG"))); // NOI18N
        jToggleButtonPeaoPreto.setToolTipText("Adiciona Peão Preto");
        jToggleButtonPeaoPreto.setFocusable(false);
        jToggleButtonPeaoPreto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonPeaoPreto.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonPeaoPreto.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonPeaoPreto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonPeaoPreto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPeaoPretoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonPeaoPreto);

        buttonGroupPecas.add(jToggleButtonTorrePreta);
        jToggleButtonTorrePreta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/r21s.PNG"))); // NOI18N
        jToggleButtonTorrePreta.setToolTipText("Adiciona Torre Preta");
        jToggleButtonTorrePreta.setFocusable(false);
        jToggleButtonTorrePreta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonTorrePreta.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonTorrePreta.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonTorrePreta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonTorrePreta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTorrePretaActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonTorrePreta);

        buttonGroupPecas.add(jToggleButtonCavaloPreto);
        jToggleButtonCavaloPreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/n21s.PNG"))); // NOI18N
        jToggleButtonCavaloPreto.setToolTipText("Adiciona Cavalo Preto");
        jToggleButtonCavaloPreto.setFocusable(false);
        jToggleButtonCavaloPreto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonCavaloPreto.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonCavaloPreto.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonCavaloPreto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonCavaloPreto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCavaloPretoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonCavaloPreto);

        buttonGroupPecas.add(jToggleButtonBispoPreto);
        jToggleButtonBispoPreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/b21s.PNG"))); // NOI18N
        jToggleButtonBispoPreto.setToolTipText("Adiciona Bispo Preto");
        jToggleButtonBispoPreto.setFocusable(false);
        jToggleButtonBispoPreto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonBispoPreto.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBispoPreto.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBispoPreto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonBispoPreto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBispoPretoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonBispoPreto);

        buttonGroupPecas.add(jToggleButtonDamaPreta);
        jToggleButtonDamaPreta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/q21s.PNG"))); // NOI18N
        jToggleButtonDamaPreta.setToolTipText("Adiciona Dama Preta");
        jToggleButtonDamaPreta.setFocusable(false);
        jToggleButtonDamaPreta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonDamaPreta.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonDamaPreta.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonDamaPreta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonDamaPreta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonDamaPretaActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonDamaPreta);

        buttonGroupPecas.add(jToggleButtonReiPreto);
        jToggleButtonReiPreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/k21s.PNG"))); // NOI18N
        jToggleButtonReiPreto.setToolTipText("Adiciona Rei Preto");
        jToggleButtonReiPreto.setFocusable(false);
        jToggleButtonReiPreto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonReiPreto.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonReiPreto.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonReiPreto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonReiPreto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonReiPretoActionPerformed(evt);
            }
        });
        jToolBarPecas.add(jToggleButtonReiPreto);

        jPanelComplementoFEN.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroupJogadorAtivo.add(jRadioButtonBrancas);
        jRadioButtonBrancas.setText("Brancas");
        jRadioButtonBrancas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonBrancasActionPerformed(evt);
            }
        });

        buttonGroupJogadorAtivo.add(jRadioButtonPretas);
        jRadioButtonPretas.setText("Pretas");
        jRadioButtonPretas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPretasActionPerformed(evt);
            }
        });

        jLabel16.setText("Cor que irá jogar");

        jLabel11.setText("Casa en Passant");

        jComboBoxCasaEnPassant.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6" }));
        jComboBoxCasaEnPassant.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxCasaEnPassant.setMaximumSize(new java.awt.Dimension(38, 22));
        jComboBoxCasaEnPassant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCasaEnPassantActionPerformed(evt);
            }
        });

        jLabel12.setText("Quantidade de Movimentos");

        jSpinnerQuantidadeMovimentos.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        jSpinnerQuantidadeMovimentos.setMaximumSize(new java.awt.Dimension(40, 22));
        jSpinnerQuantidadeMovimentos.setMinimumSize(new java.awt.Dimension(40, 22));
        jSpinnerQuantidadeMovimentos.setPreferredSize(new java.awt.Dimension(40, 22));
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerQuantidadeMovimentos, null);
        jSpinnerQuantidadeMovimentos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerQuantidadeMovimentosStateChanged(evt);
            }
        });

        jLabel14.setText("Quantidade de Jogadas");

        jSpinnerQuantidadeJogadas.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        jSpinnerQuantidadeJogadas.setMaximumSize(new java.awt.Dimension(40, 22));
        jSpinnerQuantidadeJogadas.setMinimumSize(new java.awt.Dimension(40, 22));
        jSpinnerQuantidadeJogadas.setPreferredSize(new java.awt.Dimension(40, 22));
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerQuantidadeJogadas, null);
        jSpinnerQuantidadeJogadas.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerQuantidadeJogadasStateChanged(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Roqueamento Possível"));

        jCheckBoxRoqueMaiorBrancas.setText("Roque Maior BRANCAS");
        jCheckBoxRoqueMaiorBrancas.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRoqueMaiorBrancas.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxRoqueMaiorBrancas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRoqueMaiorBrancasActionPerformed(evt);
            }
        });

        jCheckBoxRoqueMaiorPretas.setText("Roque Maior PRETAS");
        jCheckBoxRoqueMaiorPretas.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRoqueMaiorPretas.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxRoqueMaiorPretas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRoqueMaiorPretasActionPerformed(evt);
            }
        });

        jCheckBoxRoqueMenorBrancas.setText("Roque Menor BRANCAS");
        jCheckBoxRoqueMenorBrancas.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRoqueMenorBrancas.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxRoqueMenorBrancas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRoqueMenorBrancasActionPerformed(evt);
            }
        });

        jCheckBoxRoqueMenorPretas.setText("Roque Menor PRETAS");
        jCheckBoxRoqueMenorPretas.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRoqueMenorPretas.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxRoqueMenorPretas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRoqueMenorPretasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBoxRoqueMaiorBrancas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxRoqueMenorBrancas, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBoxRoqueMaiorPretas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxRoqueMenorPretas, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxRoqueMaiorBrancas)
                    .addComponent(jCheckBoxRoqueMaiorPretas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxRoqueMenorBrancas)
                    .addComponent(jCheckBoxRoqueMenorPretas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelComplementoFENLayout = new javax.swing.GroupLayout(jPanelComplementoFEN);
        jPanelComplementoFEN.setLayout(jPanelComplementoFENLayout);
        jPanelComplementoFENLayout.setHorizontalGroup(
            jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelComplementoFENLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelComplementoFENLayout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonBrancas, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonPretas, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelComplementoFENLayout.createSequentialGroup()
                        .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSpinnerQuantidadeMovimentos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addComponent(jComboBoxCasaEnPassant, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSpinnerQuantidadeJogadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(23, 23, 23))))
        );
        jPanelComplementoFENLayout.setVerticalGroup(
            jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelComplementoFENLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jRadioButtonBrancas)
                    .addComponent(jRadioButtonPretas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBoxCasaEnPassant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinnerQuantidadeMovimentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanelComplementoFENLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jSpinnerQuantidadeJogadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setText("  Defina aqui a classificação do tabuleiro:");

        jSliderVantagem.setMajorTickSpacing(1);
        jSliderVantagem.setMaximum(6);
        jSliderVantagem.setMinorTickSpacing(1);
        jSliderVantagem.setPaintTicks(true);
        jSliderVantagem.setSnapToTicks(true);
        jSliderVantagem.setToolTipText("Use este controle para definir uma avaliação do tabuleiro");
        jSliderVantagem.setValue(SituacaoJogoDAO.indiceTipo(situacaoJogo.getTipo())-1);
        jSliderVantagem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderVantagemStateChanged(evt);
            }
        });

        jLabelDescricaoVantagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDescricaoVantagem.setText(situacaoJogo.getTipo().getDescricao());

        jPanelLayoutNull.setLayout(null);

        jPanelTabuleiro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelTabuleiro.setMaximumSize(new java.awt.Dimension(330, 280));
        jPanelTabuleiro.setMinimumSize(new java.awt.Dimension(330, 280));
        jPanelTabuleiro.add(desenhaSituacaoJogo);

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

        jLabel6.setText("  Notação FEN (Forsyth-Edwards Notation)");

        jTextFieldFEN.setHorizontalAlignment(JTextField.CENTER);
        jTextFieldFEN.setBackground(new java.awt.Color(255, 255, 204));

        jToolBarApagar.setFloatable(false);
        jToolBarApagar.setOrientation(javax.swing.SwingConstants.VERTICAL);

        buttonGroupPecas.add(jToggleButtonBorracha);
        jToggleButtonBorracha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/borracha.png"))); // NOI18N
        jToggleButtonBorracha.setToolTipText("Apaga Peça");
        jToggleButtonBorracha.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBorracha.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBorracha.setPreferredSize(new java.awt.Dimension(29, 27));
        jToggleButtonBorracha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBorrachaActionPerformed(evt);
            }
        });
        jToolBarApagar.add(jToggleButtonBorracha);

        javax.swing.GroupLayout jPanelDadosTabuleiroLayout = new javax.swing.GroupLayout(jPanelDadosTabuleiro);
        jPanelDadosTabuleiro.setLayout(jPanelDadosTabuleiroLayout);
        jPanelDadosTabuleiroLayout.setHorizontalGroup(
            jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosTabuleiroLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosTabuleiroLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jTextFieldFEN)
                        .addContainerGap())
                    .addGroup(jPanelDadosTabuleiroLayout.createSequentialGroup()
                        .addGroup(jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jToolBarPecas, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosTabuleiroLayout.createSequentialGroup()
                                .addComponent(jToolBarApagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelComplementoFEN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSliderVantagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        jPanelDadosTabuleiroLayout.setVerticalGroup(
            jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosTabuleiroLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelDadosTabuleiroLayout.createSequentialGroup()
                        .addComponent(jToolBarPecas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jToolBarApagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelDadosTabuleiroLayout.createSequentialGroup()
                        .addComponent(jPanelComplementoFEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderVantagem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDadosTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldFEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelDadosPrincipaisLayout = new javax.swing.GroupLayout(jPanelDadosPrincipais);
        jPanelDadosPrincipais.setLayout(jPanelDadosPrincipaisLayout);
        jPanelDadosPrincipaisLayout.setHorizontalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldNomeSituacaoJogo)
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(51, 51, 51)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jTextFieldNomeAutor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonDadosAutor))))
                    .addComponent(jPanelDadosTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelDadosPrincipaisLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextFieldDataCriacao, jTextFieldDataModificacao, jTextFieldVersao});

        jPanelDadosPrincipaisLayout.setVerticalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldNomeSituacaoJogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonDadosAutor)
                    .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelDadosTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Dados Principais", jPanelDadosPrincipais);

        jButtonAbrirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirAnotacao.setText("Abrir");
        jButtonAbrirAnotacao.setEnabled(false);
        jButtonAbrirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonAbrirAnotacao);

        jButtonAdicionarAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonAdicionarAnotacao.setText("Adicionar");
        jButtonAdicionarAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonAdicionarAnotacao);

        jButtonExcluirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirAnotacao.setText("Excluir");
        jButtonExcluirAnotacao.setEnabled(false);
        jButtonExcluirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonExcluirAnotacao);

        jListAnotacoes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListAnotacoes.setCellRenderer(new br.ufpr.inf.heuchess.telas.editorheuristica.RenderListaAnotacoes());
        jListAnotacoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListAnotacoesMouseClicked(evt);
            }
        });
        jListAnotacoes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAnotacoesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jListAnotacoes);

        jLabelTituloListaAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTituloListaAnotacoes.setText("Anotações Gerais Sobre esta Situação de Jogo");

        jLabelTotalAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTotalAnotacoes.setText("- Total de 0");

        javax.swing.GroupLayout jPanelAnotacoesLayout = new javax.swing.GroupLayout(jPanelAnotacoes);
        jPanelAnotacoes.setLayout(jPanelAnotacoesLayout);
        jPanelAnotacoesLayout.setHorizontalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAnotacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelAnotacoesLayout.createSequentialGroup()
                        .addComponent(jLabelTituloListaAnotacoes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTotalAnotacoes)
                        .addGap(0, 394, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelAnotacoesLayout.setVerticalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAnotacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTituloListaAnotacoes)
                    .addComponent(jLabelTotalAnotacoes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Anotações", jPanelAnotacoes);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Código Gerado desta Situação de Jogo");

        jTextAreaCodigoGerado.setColumns(20);
        jTextAreaCodigoGerado.setEditable(false);
        jTextAreaCodigoGerado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextAreaCodigoGerado.setLineWrap(true);
        jTextAreaCodigoGerado.setRows(5);
        jTextAreaCodigoGerado.setWrapStyleWord(true);
        jTextAreaCodigoGerado.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jTextAreaCodigoGerado);

        javax.swing.GroupLayout jPanelCodigoGeradoLayout = new javax.swing.GroupLayout(jPanelCodigoGerado);
        jPanelCodigoGerado.setLayout(jPanelCodigoGeradoLayout);
        jPanelCodigoGeradoLayout.setHorizontalGroup(
            jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoGeradoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanelCodigoGeradoLayout.setVerticalGroup(
            jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoGeradoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Código Gerado", jPanelCodigoGerado);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPanePrincipal)
                    .addComponent(jPanelBotoesInferior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jTabbedPanePrincipal)
                .addGap(5, 5, 5)
                .addComponent(jPanelBotoesInferior, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(769, 615));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNomeSituacaoJogoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeSituacaoJogoKeyReleased
       
        alteracao = true;
        
        setTitle("Situação de Jogo - " + jTextFieldNomeSituacaoJogo.getText());
    }//GEN-LAST:event_jTextFieldNomeSituacaoJogoKeyReleased

    private void jListAnotacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnotacoesMouseClicked
        Anotacoes.verificaDuploCliqueAnotacao(this, evt);        
    }//GEN-LAST:event_jListAnotacoesMouseClicked

    private void jListAnotacoesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAnotacoesValueChanged
        Anotacoes.verificaSelecaoAnotacao(this);        
    }//GEN-LAST:event_jListAnotacoesValueChanged

    private void jButtonExcluirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirAnotacaoActionPerformed
        Anotacoes.confirmaApagarAnotacaoSelecionada(this);        
    }//GEN-LAST:event_jButtonExcluirAnotacaoActionPerformed

    private void jButtonAdicionarAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarAnotacaoActionPerformed
       adicionaNovaAnotacao();
    }//GEN-LAST:event_jButtonAdicionarAnotacaoActionPerformed

    private void jButtonAbrirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAnotacaoActionPerformed
        Anotacoes.abrirAnotacao(this);
    }//GEN-LAST:event_jButtonAbrirAnotacaoActionPerformed

    private void jTabbedPanePrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanePrincipalStateChanged

        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelCodigoGerado){
            
            StringBuilder builder = new StringBuilder();
            
            builder.append(DHJOG.TXT_SITUACAO_JOGO);
            builder.append(" \"");
            builder.append(jTextFieldNomeSituacaoJogo.getText());
            builder.append("\"\n");
            builder.append("   ");
            builder.append(DHJOG.TXT_VANTAGEM);
            builder.append(' ');
            builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
            builder.append(" \"");
            builder.append(jLabelDescricaoVantagem.getText().toUpperCase());
            builder.append("\"\n");
            builder.append("   ");
            builder.append(DHJOG.TXT_FEN);
            builder.append(' ');
            builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
            builder.append(" \"");
            builder.append(jTextFieldFEN.getText());
            builder.append("\"\n");
            builder.append(DHJOG.TXT_FIM);
            builder.append(' ');
            builder.append(DHJOG.TXT_SITUACAO_JOGO);
                         
            jTextAreaCodigoGerado.setText(builder.toString());
            
        }else
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelDadosPrincipais){
                
                jTextFieldVersao.setText(String.valueOf(situacaoJogo.getVersao()));                
                jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(situacaoJogo.getDataUltimaModificacao()));
            }
    }//GEN-LAST:event_jTabbedPanePrincipalStateChanged
     
    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed

        if (alteracao || (nova && !criadaNovaBanco)){
            
            if (!salvarEntrada("")){
                return;
            }
            
            try{
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                if (nova && !criadaNovaBanco) {
                    SituacaoJogoDAO.adiciona(situacaoJogo);
                } else {
                    SituacaoJogoDAO.atualiza(situacaoJogo);
                }
                
                ConexaoDBHeuChess.commit(); 
                
                HeuChess.telaPrincipal.fechandoTelaSituacaoJogo(situacaoJogo);                
                
            }catch(Exception e){
                HeuChess.desfazTransacao(e);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar a Situação de Jogo no Banco de Dados\nOperação Cancelada!");
                return;
            }
        }
        
        dispose();
        HeuChess.telaPrincipal.desvincultarTrazerOutraTelaFrente(this);
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaSituacaoJogo");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jToggleButtonPeaoBrancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonPeaoBrancoActionPerformed
        
        if (jToggleButtonPeaoBranco.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.PEAO_BRANCO);            
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonPeaoBrancoActionPerformed

    private void jToggleButtonTorreBrancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTorreBrancaActionPerformed
        
        if (jToggleButtonTorreBranca.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.TORRE_BRANCA);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonTorreBrancaActionPerformed

    private void jToggleButtonCavaloBrancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonCavaloBrancoActionPerformed
        
        if (jToggleButtonCavaloBranco.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.CAVALO_BRANCO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonCavaloBrancoActionPerformed

    private void jToggleButtonBispoBrancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBispoBrancoActionPerformed
        
        if (jToggleButtonBispoBranco.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.BISPO_BRANCO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonBispoBrancoActionPerformed

    private void jToggleButtonDamaBrancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonDamaBrancaActionPerformed
        
        if (jToggleButtonDamaBranca.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.DAMA_BRANCA);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonDamaBrancaActionPerformed

    private void jToggleButtonReiBrancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonReiBrancoActionPerformed
        
        if (jToggleButtonReiBranco.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.REI_BRANCO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonReiBrancoActionPerformed

    private void jToggleButtonReiPretoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonReiPretoActionPerformed
        
        if (jToggleButtonReiPreto.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.REI_PRETO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }        
    }//GEN-LAST:event_jToggleButtonReiPretoActionPerformed

    private void jToggleButtonDamaPretaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonDamaPretaActionPerformed
        
        if (jToggleButtonDamaPreta.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.DAMA_PRETA);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonDamaPretaActionPerformed

    private void jToggleButtonBispoPretoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBispoPretoActionPerformed
        
        if (jToggleButtonBispoPreto.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.BISPO_PRETO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonBispoPretoActionPerformed

    private void jToggleButtonCavaloPretoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonCavaloPretoActionPerformed
        
        if (jToggleButtonCavaloPreto.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.CAVALO_PRETO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonCavaloPretoActionPerformed

    private void jToggleButtonTorrePretaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTorrePretaActionPerformed
        
        if (jToggleButtonTorrePreta.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.TORRE_PRETA);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonTorrePretaActionPerformed

    private void jToggleButtonPeaoPretoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonPeaoPretoActionPerformed
        
        if (jToggleButtonPeaoPreto.isSelected()) {
            desenhaSituacaoJogo.setPecaEscolhida(Peca.PEAO_PRETO);
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonPeaoPretoActionPerformed

    private void jCheckBoxRoqueMaiorBrancasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRoqueMaiorBrancasActionPerformed
        desenhaSituacaoJogo.setRoqueMaiorBrancas(jCheckBoxRoqueMaiorBrancas.isSelected());
    }//GEN-LAST:event_jCheckBoxRoqueMaiorBrancasActionPerformed

    private void jCheckBoxRoqueMenorBrancasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRoqueMenorBrancasActionPerformed
        desenhaSituacaoJogo.setRoqueMenorBrancas(jCheckBoxRoqueMenorBrancas.isSelected());
    }//GEN-LAST:event_jCheckBoxRoqueMenorBrancasActionPerformed

    private void jCheckBoxRoqueMaiorPretasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRoqueMaiorPretasActionPerformed
        desenhaSituacaoJogo.setRoqueMaiorPretas(jCheckBoxRoqueMaiorPretas.isSelected());
    }//GEN-LAST:event_jCheckBoxRoqueMaiorPretasActionPerformed

    private void jCheckBoxRoqueMenorPretasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRoqueMenorPretasActionPerformed
        desenhaSituacaoJogo.setRoqueMenorPretas(jCheckBoxRoqueMenorPretas.isSelected());
    }//GEN-LAST:event_jCheckBoxRoqueMenorPretasActionPerformed

    private void jSliderVantagemStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderVantagemStateChanged
        
        Tipo tipo = SituacaoJogoDAO.tiposSituacaoJogo.get(jSliderVantagem.getValue());
        jLabelDescricaoVantagem.setText(tipo.getDescricao());        
        
        alteracao = true;
    }//GEN-LAST:event_jSliderVantagemStateChanged

    private void jRadioButtonBrancasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonBrancasActionPerformed
        desenhaSituacaoJogo.setJogadorBrancoAtivo(true);
    }//GEN-LAST:event_jRadioButtonBrancasActionPerformed

    private void jRadioButtonPretasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPretasActionPerformed
        desenhaSituacaoJogo.setJogadorBrancoAtivo(false);
    }//GEN-LAST:event_jRadioButtonPretasActionPerformed

    private void jComboBoxCasaEnPassantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCasaEnPassantActionPerformed

        try{
            desenhaSituacaoJogo.setCasaEnPassant((String) jComboBoxCasaEnPassant.getSelectedItem());
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(this, "Erro ao tentar definir Casa En Passant!\n" + e.getMessage());            
        }
    }//GEN-LAST:event_jComboBoxCasaEnPassantActionPerformed

    private void jSpinnerQuantidadeMovimentosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerQuantidadeMovimentosStateChanged
        desenhaSituacaoJogo.setQuantidadeMovimentos(((Integer)jSpinnerQuantidadeMovimentos.getValue()).intValue());
    }//GEN-LAST:event_jSpinnerQuantidadeMovimentosStateChanged

    private void jSpinnerQuantidadeJogadasStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerQuantidadeJogadasStateChanged
        desenhaSituacaoJogo.setQuantidadeJogadas(((Integer)jSpinnerQuantidadeJogadas.getValue()).intValue());
    }//GEN-LAST:event_jSpinnerQuantidadeJogadasStateChanged

    private void jToggleButtonBorrachaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBorrachaActionPerformed
        
        if (jToggleButtonBorracha.isSelected()) {
            desenhaSituacaoJogo.setBorracha();
        } else {
            desenhaSituacaoJogo.desativaSelecaoPeca();
        }
    }//GEN-LAST:event_jToggleButtonBorrachaActionPerformed

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        HeuChess.dadosAutor(this, situacaoJogo.getIdAutor());
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed

    private void jButtonEntradaFENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEntradaFENActionPerformed
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaEntradaFEN tela = new TelaEntradaFEN(TelaSituacaoJogo.this, desenhaSituacaoJogo.getFEN());                
            }
        });
    }//GEN-LAST:event_jButtonEntradaFENActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupJogadorAtivo;
    private javax.swing.ButtonGroup buttonGroupPecas;
    private javax.swing.JButton jButtonAbrirAnotacao;
    private javax.swing.JButton jButtonAdicionarAnotacao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JButton jButtonEntradaFEN;
    private javax.swing.JButton jButtonExcluirAnotacao;
    private javax.swing.JCheckBox jCheckBoxRoqueMaiorBrancas;
    private javax.swing.JCheckBox jCheckBoxRoqueMaiorPretas;
    private javax.swing.JCheckBox jCheckBoxRoqueMenorBrancas;
    private javax.swing.JCheckBox jCheckBoxRoqueMenorPretas;
    private javax.swing.JComboBox jComboBoxCasaEnPassant;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescricaoVantagem;
    private javax.swing.JLabel jLabelTituloListaAnotacoes;
    private javax.swing.JLabel jLabelTotalAnotacoes;
    private javax.swing.JList jListAnotacoes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelBotoesAnotacao;
    private javax.swing.JPanel jPanelBotoesInferior;
    private javax.swing.JPanel jPanelCentralizaBotaoEntradaFEN;
    private javax.swing.JPanel jPanelCodigoGerado;
    private javax.swing.JPanel jPanelComplementoFEN;
    private javax.swing.JPanel jPanelDadosPrincipais;
    private javax.swing.JPanel jPanelDadosTabuleiro;
    private javax.swing.JPanel jPanelLayoutNull;
    private javax.swing.JPanel jPanelTabuleiro;
    private javax.swing.JRadioButton jRadioButtonBrancas;
    private javax.swing.JRadioButton jRadioButtonPretas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSliderVantagem;
    private javax.swing.JSpinner jSpinnerQuantidadeJogadas;
    private javax.swing.JSpinner jSpinnerQuantidadeMovimentos;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JTextArea jTextAreaCodigoGerado;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldFEN;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldNomeSituacaoJogo;
    private javax.swing.JTextField jTextFieldVersao;
    private javax.swing.JToggleButton jToggleButtonBispoBranco;
    private javax.swing.JToggleButton jToggleButtonBispoPreto;
    private javax.swing.JToggleButton jToggleButtonBorracha;
    private javax.swing.JToggleButton jToggleButtonCavaloBranco;
    private javax.swing.JToggleButton jToggleButtonCavaloPreto;
    private javax.swing.JToggleButton jToggleButtonDamaBranca;
    private javax.swing.JToggleButton jToggleButtonDamaPreta;
    private javax.swing.JToggleButton jToggleButtonPeaoBranco;
    private javax.swing.JToggleButton jToggleButtonPeaoPreto;
    private javax.swing.JToggleButton jToggleButtonReiBranco;
    private javax.swing.JToggleButton jToggleButtonReiPreto;
    private javax.swing.JToggleButton jToggleButtonTorreBranca;
    private javax.swing.JToggleButton jToggleButtonTorrePreta;
    private javax.swing.JToolBar jToolBarApagar;
    private javax.swing.JToolBar jToolBarPecas;
    // End of variables declaration//GEN-END:variables
}
