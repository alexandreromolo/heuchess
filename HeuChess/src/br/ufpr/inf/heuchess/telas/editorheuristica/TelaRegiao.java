package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.RegiaoDAO;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao.DesenhaRegioes;
import br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao.Ponto;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 30 de Julho de 2006, 17:02
 */
public class TelaRegiao extends javax.swing.JFrame implements AcessoTelaAnotacao, AcessoTelaUsuario, PropertyChangeListener {
    
    private PanelEtapa       panelEtapa;
    private DesenhaRegioes   panelTabuleiro;
    private AcessoTelaRegiao acessoTelaRegiao;
    
    private boolean nova;
    private boolean alteracao;
    private boolean criadaNovaBanco;
    
    private Regiao  regiao;
    private Regiao  regiaoOriginal;
    
    private Color corNormal, corSimetrica;    
    
    private DesenhaRegioes.Ferramenta backupFerramentaAtual;
    
    private boolean iniciandoInvisivelNovaAnotacao = false;
    
    /***
     * Construtor chamado quando se está criando uma nova Região
     */
    public TelaRegiao(PanelEtapa panelEtapa, AcessoTelaRegiao acessoTelaRegiao) {
        
        this.panelEtapa       = panelEtapa;
        this.acessoTelaRegiao = acessoTelaRegiao;
        
        nova = true;
        iniciandoInvisivelNovaAnotacao = false;
        
        corNormal    = panelEtapa.editor.colorList.nextColor();
        corSimetrica = panelEtapa.editor.colorList.nextColor();
        
        panelTabuleiro = new DesenhaRegioes(corNormal);                    
        regiao         = new Regiao("",
                                    HeuChess.usuario.getId(),
                                    RegiaoDAO.tiposRegiao.get(0));
        try {
            montaInterface();    
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(acessoTelaRegiao.getFrame(), "Erro ao buscar dados da Região no Banco de Dados!");
            dispose();
        }
    }
    
    /**
     * Construtora chamado passando como parâmetro uma região já existente
     */
    public TelaRegiao(PanelEtapa panelEtapa, AcessoTelaRegiao acessoTelaRegiao, Regiao regiaoOriginal, boolean novaAnotacao) {
        
        this.panelEtapa       = panelEtapa;
        this.acessoTelaRegiao = acessoTelaRegiao;  
                
        nova = false;
        iniciandoInvisivelNovaAnotacao = novaAnotacao;
        
        try{
            this.regiaoOriginal = regiaoOriginal;                
            regiao = regiaoOriginal.geraClone();
        
            corNormal    = regiaoOriginal.getColor();
            corSimetrica = panelEtapa.editor.colorList.nextColor();
        
            panelTabuleiro = new DesenhaRegioes(corNormal);                    
        
            for (Casa atual : regiao.getCasas()){
                panelTabuleiro.addElement(new Ponto(panelTabuleiro, atual, corNormal));
            }
            
            montaInterface();
            
        }catch(Exception e){
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(acessoTelaRegiao.getFrame(), "Erro ao buscar dados da Região no Banco de Dados!");
            dispose();
        }
    }
    
    @Override
    public Frame getFrame(){
        
        if (iniciandoInvisivelNovaAnotacao) {
            return acessoTelaRegiao.getFrame();
        } else {
            return this;
        }
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        
        if (iniciandoInvisivelNovaAnotacao) {
            return acessoTelaRegiao.getModalOwner();
        } else {
            return acessoTelaRegiao;
        }
    }
    
    private void montaInterface() throws Exception {
        
        initComponents();   
        
        jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(regiao.getIdAutor())));
        
        setTitle("Região - " + jTextFieldNomeRegiao.getText());        
        alteracao = false;
        
        atualizaInterfaceNivelComplexidade();
        Anotacoes.atualizaQuantidadeAnotacoes(this);
        
        if (!panelEtapa.editor.podeAlterar()){
            jTextFieldNomeRegiao.setEditable(false);
            
            jSeparatorMostrarRegiaoSimetrica.setVisible(false);
            jToggleButtonBorracha.setVisible(false);
            jToggleButtonLapis.setVisible(false);
            jToggleButtonRect.setVisible(false);
            jToggleButtonSeleciona.setVisible(false);
                
            jButtonExcluirAnotacao.setVisible(false);
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
        
        if (!panelEtapa.editor.podeAnotar()){
            jButtonNovaAnotacao.setVisible(false);
            
            if (regiao.getAnotacoes().isEmpty()){
                jTabbedPanePrincipal.remove(jPanelAnotacoes);
            }
        }
        
        panelTabuleiro.addPropertyChangeListener(this);
        
        if (iniciandoInvisivelNovaAnotacao){
            adicionaNovaAnotacao();
        }else{
            
            if (!nova){
                Historico.registraComponenteAberto(regiao);
                ConexaoDBHeuChess.commit();
            }
            
            ModalFrameUtil.showAsModalDontBlock(this);
            jTextFieldNomeRegiao.requestFocus();
        }
    }
    
    private void atualizaInterfaceNivelComplexidade(){        
        
        Tipo complexidade = panelEtapa.editor.conjuntoHeuristico.getTipo();
        
        if (complexidade == ConjuntoHeuristico.NIVEL_1_INICIANTE ||
            complexidade == ConjuntoHeuristico.NIVEL_2_BASICO    ||
            complexidade == ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO){
            
            jTabbedPanePrincipal.remove(jPanelCodigoGerado);
        }else{
            jTabbedPanePrincipal.remove(jPanelCodigoGerado);
            jTabbedPanePrincipal.add("Código Gerado", jPanelCodigoGerado);
        }
    }
        
    private void confirmaCancelar(){
        
        if (panelEtapa.editor.podeAlterar() || panelEtapa.editor.podeAnotar()) {
            
            if (alteracao || criadaNovaBanco) {
                
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?",
                                                                 "Confirmação Cancelamento");
                
                if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                    return;
                }
                
                if (nova) {
                    panelEtapa.editor.colorList.previousColor();
                }

                if (criadaNovaBanco) {                    
                    try {
                        setCursor(new Cursor(Cursor.WAIT_CURSOR));

                        RegiaoDAO.apaga(regiao, panelEtapa.etapa);
                        
                        ConexaoDBHeuChess.commit();

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        
                        dispose();        
                        acessoTelaRegiao.fechandoTelaRegiao(null); 
                        return;
                        
                    } catch (Exception e) {
                        HeuChess.desfazTransacao(e);

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        UtilsGUI.dialogoErro(this, "Erro ao tentar desfazer alterações no Banco de Dados\nOperação Cancelada!");                        
                    }
                }
            }
        }
        
        verificaAlteracoesAnotacoesAoSair();
    }
    
    private void verificaAlteracoesAnotacoesAoSair() {
        
        if (!nova && Anotacoes.anotacoesDiferentes(regiao, regiaoOriginal)) {

            /////////////////////////////////                    
            // Apenas alterou as Anotações //
            /////////////////////////////////

            ModelListaComponentes modelRegioes = panelEtapa.getModelRegioes();

            int posicao = panelEtapa.etapa.getRegioes().indexOf(regiaoOriginal);

            modelRegioes.set(posicao, regiao);

            panelEtapa.selecionaRegiao(posicao);

            dispose();
            acessoTelaRegiao.fechandoTelaRegiao(regiao);

        } else {
            
            dispose();
            acessoTelaRegiao.fechandoTelaRegiao(null);
        }
    }
    
    public void adicionaNovaAnotacao(){
        
        if (nova && !criadaNovaBanco){
            
            if (!salvarEntrada("\n\nÉ preciso tornar a Região válida antes de criar uma Anotação para ela!")){                
                return;
            }
            
            try{
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                RegiaoDAO.adiciona(panelEtapa.etapa, regiao);
                        
                ConexaoDBHeuChess.commit();
                
                criadaNovaBanco = true;
                alteracao       = false;
                jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(regiao.getDataCriacao()));
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            }catch(Exception e){
                HeuChess.desfazTransacao(e);                
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar criar a Região no Banco de Dados\nOperação Cancelada!");                
                return;
            }
        }        
    
        Anotacoes.novaAnotacao(this);          
    }
    
    private boolean salvarEntrada(String complemento){
       
        String nome = jTextFieldNomeRegiao.getText();
        if (nome == null || nome.trim().length() == 0){
            UtilsGUI.dialogoErro(this, "O Nome da Região não esta preenchido.\n" +
                                       "Uma Região precisa ter um nome definido para poder ser salva!"+complemento);
            return false;            
        }
            
        if (panelTabuleiro.totalElementos() == 0){
            UtilsGUI.dialogoErro(this, "A Região não possui nenhuma Casa marcada.\n" +
                                       "Uma Região precisa ter pelo menos uma Casa marcada para poder ser salva!" + complemento);
            return false;            
        }
            
        String erro = panelEtapa.editor.conjuntoHeuristico.validaNomeUnicoComponente(nome);
            
        if ((erro != null) && ((nova && !criadaNovaBanco) ||
                              (!nova && !regiaoOriginal.getNome().equalsIgnoreCase(nome)) ||
                              (nova  && criadaNovaBanco && !regiao.getNome().equalsIgnoreCase(nome)))){                            
            
             UtilsGUI.dialogoErro(this, erro + complemento);
             
             if (!nova){
                 jTextFieldNomeRegiao.setText(regiao.getNome());
             }
             
             jTextFieldNomeRegiao.selectAll();
             jTextFieldNomeRegiao.requestFocus();
             return false;            
        }
            
        regiao.setNome(UtilsString.preparaStringParaBD(jTextFieldNomeRegiao.getText(), true, Formato.TUDO_MAIUSCULO));
        regiao.setColorIcon(new ColorIcon(panelTabuleiro.getCorFerramenta()));
        regiao.setVisivel(true);            
        
        if (!jCheckBoxMostrarRegiaoSimetrica.isSelected()){            
            panelTabuleiro.transfereCasas(regiao);            
        }
        
        return true;
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
        return regiao;
    }

    @Override
    public boolean podeAlterar() {
        return panelEtapa.editor.podeAlterar();
    }
    
    @Override
    public void atualizaVersaoDataUltimaModificacao() {
        jTextFieldVersao.setText(String.valueOf(regiao.getVersao()));
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(regiao.getDataUltimaModificacao()));
    }
    
    @Override
    public void fechandoTelaAnotacao(boolean sucesso) {
        
        if (iniciandoInvisivelNovaAnotacao){
            
            if (sucesso){
                iniciandoInvisivelNovaAnotacao = false;
                ModalFrameUtil.showAsModalDontBlock(this);
                
                try {
                    Historico.registraComponenteAberto(regiao);
                    ConexaoDBHeuChess.commit();
                    
                } catch (Exception e) {
                    HeuChess.desfazTransacao(e);
                    
                    UtilsGUI.dialogoErro(acessoTelaRegiao.getFrame(), "Erro ao registrar ação de Abertura de Componente no Banco de Dados!");
                    dispose();
                    acessoTelaRegiao.fechandoTelaRegiao(null);
                    return;
                }
            }else{
                dispose();
                acessoTelaRegiao.fechandoTelaRegiao(regiao);
                return;
            }            
        }
        
        jTabbedPanePrincipal.setSelectedComponent(jPanelAnotacoes);
    }    
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null){
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(usuario.getNome()));
        }
    }
     
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (evt.getPropertyName().equalsIgnoreCase("elemento_apagado")){
            if (HeuChess.somAtivado){
                HeuChess.somApagar.play();
            }
        }
        
        if (evt.getPropertyName().equalsIgnoreCase("elemento_incluido")) {
            if (HeuChess.somAtivado) {
                HeuChess.somPecaColocada.play();
            }
        }
        
        if (evt.getPropertyName().equalsIgnoreCase("elemento_movido")) {
            if (HeuChess.somAtivado) {
                HeuChess.somDragAndDrop.play();
            }
        }
        
        alteracao = true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBotoesInferior = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelDadosPrincipais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNomeRegiao = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jButtonDadosAutor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldVersao = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jToolBarBotoes = new javax.swing.JToolBar();
        jToggleButtonSeleciona = new javax.swing.JToggleButton();
        jToggleButtonRect = new javax.swing.JToggleButton();
        jToggleButtonLapis = new javax.swing.JToggleButton();
        jToggleButtonBorracha = new javax.swing.JToggleButton();
        jSeparatorMostrarRegiaoSimetrica = new javax.swing.JToolBar.Separator();
        jCheckBoxMostrarRegiaoSimetrica = new javax.swing.JCheckBox();
        jPanelCentral = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanelAnotacoes = new javax.swing.JPanel();
        jPanelBotoesAnotacao = new javax.swing.JPanel();
        jButtonAbrirAnotacao = new javax.swing.JButton();
        jButtonNovaAnotacao = new javax.swing.JButton();
        jButtonExcluirAnotacao = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListAnotacoes = new JList(new ModelListaComponentes(regiao.getAnotacoes()));
        jLabelTituloListaAnotacoes = new javax.swing.JLabel();
        jLabelTotalAnotacoes = new javax.swing.JLabel();
        jPanelCodigoGerado = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaCodigoGerado = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Região - ");
        setIconImage(new ImageIcon(getClass().getResource("/icones/retangulo.png")).getImage());
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

        javax.swing.GroupLayout jPanelBotoesInferiorLayout = new javax.swing.GroupLayout(jPanelBotoesInferior);
        jPanelBotoesInferior.setLayout(jPanelBotoesInferiorLayout);
        jPanelBotoesInferiorLayout.setHorizontalGroup(
            jPanelBotoesInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBotoesInferiorLayout.createSequentialGroup()
                .addComponent(jButtonAjuda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(10, 10, 10))
        );

        jPanelBotoesInferiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jTabbedPanePrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPanePrincipalStateChanged(evt);
            }
        });

        jLabel1.setText("Nome");

        jTextFieldNomeRegiao.setDocument(new DocumentMasked(DHJOG.CARACTERES_VALIDOS,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNomeRegiao.setText(regiao.getNome());
        jTextFieldNomeRegiao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeRegiaoKeyReleased(evt);
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
        jTextFieldVersao.setText(String.valueOf(regiao.getVersao()));

        jLabel4.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(regiao.getDataCriacao()));

        jLabel5.setText("Modificação");

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(regiao.getDataUltimaModificacao()));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jToolBarBotoes.setFloatable(false);

        jToggleButtonSeleciona.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_selecionar.png"))); // NOI18N
        jToggleButtonSeleciona.setToolTipText("Movimenta uma Região");
        jToggleButtonSeleciona.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonSeleciona.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonSeleciona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSelecionaActionPerformed(evt);
            }
        });
        jToolBarBotoes.add(jToggleButtonSeleciona);

        jToggleButtonRect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/retangulo.png"))); // NOI18N
        jToggleButtonRect.setToolTipText("Desenha um Retângulo");
        jToggleButtonRect.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonRect.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonRect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonRectActionPerformed(evt);
            }
        });
        jToolBarBotoes.add(jToggleButtonRect);

        jToggleButtonLapis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_lapiz.png"))); // NOI18N
        jToggleButtonLapis.setToolTipText("Marca uma Casa");
        jToggleButtonLapis.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonLapis.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonLapis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonLapisActionPerformed(evt);
            }
        });
        jToolBarBotoes.add(jToggleButtonLapis);

        jToggleButtonBorracha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/borracha.png"))); // NOI18N
        jToggleButtonBorracha.setToolTipText("Apaga uma Região");
        jToggleButtonBorracha.setMaximumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBorracha.setMinimumSize(new java.awt.Dimension(29, 27));
        jToggleButtonBorracha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBorrachaActionPerformed(evt);
            }
        });
        jToolBarBotoes.add(jToggleButtonBorracha);
        jToolBarBotoes.add(jSeparatorMostrarRegiaoSimetrica);

        jCheckBoxMostrarRegiaoSimetrica.setText("Mostrar Região Simétrica");
        jCheckBoxMostrarRegiaoSimetrica.setFocusable(false);
        jCheckBoxMostrarRegiaoSimetrica.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCheckBoxMostrarRegiaoSimetrica.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCheckBoxMostrarRegiaoSimetrica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMostrarRegiaoSimetricaActionPerformed(evt);
            }
        });
        jToolBarBotoes.add(jCheckBoxMostrarRegiaoSimetrica);

        jPanelCentral.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("<html>\n<br>\nDefine uma Região<br>\nimaginando que você<br>\nirá jogar com as peças<br>\n<b>Brancas</b>.<br>\n<br>\nDurante uma partida,<br>\ncaso este Conjunto Heurístico<br>\nesteja jogando com as<br>\npeças Pretas, o programa<br>\nirá <b>converter</b> a região<br>\nautomaticamente para a sua<br>\n<b>versão simétrica</b>.<br>\n</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanelCentral, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6))
                    .addComponent(jToolBarBotoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBarBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                    .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelCentral.add(panelTabuleiro);

        javax.swing.GroupLayout jPanelDadosPrincipaisLayout = new javax.swing.GroupLayout(jPanelDadosPrincipais);
        jPanelDadosPrincipais.setLayout(jPanelDadosPrincipaisLayout);
        jPanelDadosPrincipaisLayout.setHorizontalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDadosAutor))
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldNomeRegiao))))
                .addContainerGap())
        );

        jPanelDadosPrincipaisLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextFieldDataCriacao, jTextFieldDataModificacao, jTextFieldVersao});

        jPanelDadosPrincipaisLayout.setVerticalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldNomeRegiao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jButtonNovaAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaAnotacao.setText("Adicionar");
        jButtonNovaAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonNovaAnotacao);

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
        jListAnotacoes.setCellRenderer(new RenderListaAnotacoes());
        jListAnotacoes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAnotacoesValueChanged(evt);
            }
        });
        jListAnotacoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListAnotacoesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jListAnotacoes);

        jLabelTituloListaAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTituloListaAnotacoes.setText("Anotações Gerais Sobre esta Região");

        jLabelTotalAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTotalAnotacoes.setText("- Total de 0");

        javax.swing.GroupLayout jPanelAnotacoesLayout = new javax.swing.GroupLayout(jPanelAnotacoes);
        jPanelAnotacoes.setLayout(jPanelAnotacoesLayout);
        jPanelAnotacoesLayout.setHorizontalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAnotacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelAnotacoesLayout.createSequentialGroup()
                        .addComponent(jLabelTituloListaAnotacoes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTotalAnotacoes)))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Anotações", jPanelAnotacoes);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Código Gerado desta Heurística");

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanelCodigoGeradoLayout.setVerticalGroup(
            jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoGeradoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Código Gerado", jPanelCodigoGerado);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPanePrincipal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(jPanelBotoesInferior, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPanePrincipal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-612)/2, (screenSize.height-531)/2, 612, 531);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNomeRegiaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeRegiaoKeyReleased
        alteracao = true;
        setTitle("Região - " + jTextFieldNomeRegiao.getText());
    }//GEN-LAST:event_jTextFieldNomeRegiaoKeyReleased

    private void jListAnotacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnotacoesMouseClicked
        Anotacoes.verificaDuploCliqueAnotacao(this, evt);        
    }//GEN-LAST:event_jListAnotacoesMouseClicked

    private void jListAnotacoesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAnotacoesValueChanged
        Anotacoes.verificaSelecaoAnotacao(this);        
    }//GEN-LAST:event_jListAnotacoesValueChanged

    private void jButtonExcluirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirAnotacaoActionPerformed
        Anotacoes.confirmaApagarAnotacaoSelecionada(this);        
    }//GEN-LAST:event_jButtonExcluirAnotacaoActionPerformed

    private void jButtonNovaAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaAnotacaoActionPerformed
       adicionaNovaAnotacao();
    }//GEN-LAST:event_jButtonNovaAnotacaoActionPerformed

    private void jButtonAbrirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAnotacaoActionPerformed
        Anotacoes.abrirAnotacao(this);
    }//GEN-LAST:event_jButtonAbrirAnotacaoActionPerformed

    private void jTabbedPanePrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanePrincipalStateChanged
         
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelCodigoGerado){
            
            StringBuilder builder = new StringBuilder();
            
            builder.append(DHJOG.TipoDado.CASAS);
            builder.append(" \"");
            builder.append(jTextFieldNomeRegiao.getText());
            builder.append("\" ");
            builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
            builder.append(" {");
            
            ArrayList<Casa> casas = panelTabuleiro.todasCasas();
            
            if (casas != null && casas.size() >= 1){
                
                for (int x = 0; x < casas.size()-1; x++){
                    builder.append(casas.get(x));
                    builder.append(", ");                    
                }
            
                builder.append(casas.get(casas.size()-1));
            }
            
            builder.append('}');
            
            jTextAreaCodigoGerado.setText(builder.toString());
            
        }else
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelDadosPrincipais){
                jTextFieldVersao.setText(String.valueOf(regiao.getVersao()));                
                jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(regiao.getDataUltimaModificacao()));
            }
    }//GEN-LAST:event_jTabbedPanePrincipalStateChanged
     
    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed
        
        ModelListaComponentes modelRegioes = panelEtapa.getModelRegioes();
        
        if (alteracao || (nova && !criadaNovaBanco)){
            
            if (!salvarEntrada("")){
                return;
            }
            
            try {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                if (nova && !criadaNovaBanco){
                    
                    RegiaoDAO.adiciona(panelEtapa.etapa, regiao);
                    
                }else{
                    
                    RegiaoDAO.atualiza(regiao);

                    if (regiaoOriginal != null && !regiao.getNome().equalsIgnoreCase(regiaoOriginal.getNome())) {

                        ////////////////////////////////////////////////////////
                        // Troca o objeto antigo da Região pelo novo na Etapa //
                        ////////////////////////////////////////////////////////

                        for (int x = 0; x < panelEtapa.etapa.getRegioes().size(); x++) {

                            Regiao reg = panelEtapa.etapa.getRegioes().get(x);

                            if (reg.getNome().equalsIgnoreCase(regiaoOriginal.getNome())) {
                                modelRegioes.set(x, regiao);
                                break;
                            }
                        }
                        
                        ///////////////////////////////////////////////
                        // Renomeia a Região em todas as Heurísticas //
                        ///////////////////////////////////////////////

                        for (Etapa etapa : panelEtapa.editor.conjuntoHeuristico.getEtapas()) {
                            etapa.procuraRenomeia(regiao, regiaoOriginal.getNome());                            
                        }
                        
                    }else{
                    
                        int posicao = panelEtapa.etapa.getRegioes().indexOf(regiaoOriginal);
                    
                        modelRegioes.set(posicao, regiao);
                    }
                }

                ConexaoDBHeuChess.commit();

                if (nova || criadaNovaBanco) {
                    
                    int pos = 0;
                    
                    for ( ; pos < panelEtapa.etapa.getRegioes().size(); pos++){
                        
                        Regiao reg = panelEtapa.etapa.getRegioes().get(pos);
                        
                        if (regiao.compareTo(reg) <= 0){
                            break;
                        }
                    }
                    
                    modelRegioes.add(pos, regiao);
                } 
                
                panelEtapa.selecionaRegiao(panelEtapa.etapa.getRegioes().indexOf(regiao));

                dispose();
                acessoTelaRegiao.fechandoTelaRegiao(regiao);

            } catch (Exception e) {
                HeuChess.desfazTransacao(e);
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar a Região no Banco de Dados." +
                                           "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                dispose();                
                panelEtapa.editor.fechar(true);
            }
        }else
            if (nova){
                
                ///////////////////////////////////////////////////////////////////
                // Já foi salva no banco no momento do cadastro da nova Anotação //
                ///////////////////////////////////////////////////////////////////

                int pos = 0;

                for (; pos < panelEtapa.etapa.getRegioes().size(); pos++) {

                    Regiao reg = panelEtapa.etapa.getRegioes().get(pos);

                    if (regiao.compareTo(reg) <= 0) {
                        break;
                    }
                }

                modelRegioes.add(pos, regiao);

                panelEtapa.selecionaRegiao(pos);
                
                dispose();
                acessoTelaRegiao.fechandoTelaRegiao(regiao);
                
            }else{
                verificaAlteracoesAnotacoesAoSair();
            }                
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jToggleButtonSelecionaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonSelecionaActionPerformed
        
        if (jToggleButtonSeleciona.isSelected()){
            jToggleButtonLapis.setSelected(false);
            jToggleButtonBorracha.setSelected(false);       
            jToggleButtonRect.setSelected(false);        
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.SELECAO);
        }else{
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.NENHUMA);
        }        
    }//GEN-LAST:event_jToggleButtonSelecionaActionPerformed

    private void jToggleButtonBorrachaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBorrachaActionPerformed
        
        if (jToggleButtonBorracha.isSelected()){
            jToggleButtonLapis.setSelected(false);
            jToggleButtonSeleciona.setSelected(false);        
            jToggleButtonRect.setSelected(false);        
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.BORRACHA);
        }else{
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.NENHUMA);
        }        
    }//GEN-LAST:event_jToggleButtonBorrachaActionPerformed

    private void jToggleButtonLapisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonLapisActionPerformed
        
        if (jToggleButtonLapis.isSelected()){
            jToggleButtonBorracha.setSelected(false);        
            jToggleButtonSeleciona.setSelected(false);
            jToggleButtonRect.setSelected(false);        
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.LAPIS);
        }else{
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.NENHUMA);
        }
    }//GEN-LAST:event_jToggleButtonLapisActionPerformed

    private void jToggleButtonRectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonRectActionPerformed
        
        if (jToggleButtonRect.isSelected()){
            jToggleButtonBorracha.setSelected(false);
            jToggleButtonLapis.setSelected(false);
            jToggleButtonSeleciona.setSelected(false);               
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.RETANGULO);
        }else{
            panelTabuleiro.defineFerramenta(DesenhaRegioes.Ferramenta.NENHUMA);
        }
    }//GEN-LAST:event_jToggleButtonRectActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaRegiao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        HeuChess.dadosAutor(this, regiao.getIdAutor());
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed

    private void jCheckBoxMostrarRegiaoSimetricaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMostrarRegiaoSimetricaActionPerformed
        
        if (jCheckBoxMostrarRegiaoSimetrica.isSelected()){
            
            panelTabuleiro.removePropertyChangeListener(this);
            
            panelTabuleiro.transfereCasas(regiao);
            
            backupFerramentaAtual = panelTabuleiro.getFerramenta();
            
            panelTabuleiro.reset();
            
            if (panelEtapa.editor.podeAlterar()){
                jToggleButtonBorracha.setEnabled(false);
                jToggleButtonLapis.setEnabled(false);
                jToggleButtonRect.setEnabled(false);
                jToggleButtonSeleciona.setEnabled(false);
            }
            
            for (Casa atual : regiao.getCasasSimetricas()){
                panelTabuleiro.addElement(new Ponto(panelTabuleiro,atual,corSimetrica));
            }
            
        }else{
            
            if (panelEtapa.editor.podeAlterar()){
                jToggleButtonBorracha.setEnabled(true);
                jToggleButtonLapis.setEnabled(true);
                jToggleButtonRect.setEnabled(true);
                jToggleButtonSeleciona.setEnabled(true);
            }
            
            panelTabuleiro.reset();
            
            for (Casa atual : regiao.getCasas()){
                panelTabuleiro.addElement(new Ponto(panelTabuleiro,atual,corNormal));
            }
             
            panelTabuleiro.defineFerramenta(backupFerramentaAtual);
            
            panelTabuleiro.addPropertyChangeListener(this);
        }
    }//GEN-LAST:event_jCheckBoxMostrarRegiaoSimetricaActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrirAnotacao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JButton jButtonExcluirAnotacao;
    private javax.swing.JButton jButtonNovaAnotacao;
    private javax.swing.JCheckBox jCheckBoxMostrarRegiaoSimetrica;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelTituloListaAnotacoes;
    private javax.swing.JLabel jLabelTotalAnotacoes;
    private javax.swing.JList jListAnotacoes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelBotoesAnotacao;
    private javax.swing.JPanel jPanelBotoesInferior;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JPanel jPanelCodigoGerado;
    private javax.swing.JPanel jPanelDadosPrincipais;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparatorMostrarRegiaoSimetrica;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JTextArea jTextAreaCodigoGerado;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldNomeRegiao;
    private javax.swing.JTextField jTextFieldVersao;
    private javax.swing.JToggleButton jToggleButtonBorracha;
    private javax.swing.JToggleButton jToggleButtonLapis;
    private javax.swing.JToggleButton jToggleButtonRect;
    private javax.swing.JToggleButton jToggleButtonSeleciona;
    private javax.swing.JToolBar jToolBarBotoes;
    // End of variables declaration//GEN-END:variables
}
