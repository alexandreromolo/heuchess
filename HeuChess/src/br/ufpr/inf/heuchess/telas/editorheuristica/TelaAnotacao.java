package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.persistencia.AnotacaoDAO;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 17 de Julho de 2006, 10:56
 */
public class TelaAnotacao extends javax.swing.JFrame implements AcessoTelaUsuario {
    
    private AcessoTelaAnotacao  acessoTelaAnotacao;  
    
    private Componente componente;
    private Anotacao   anotacaoOriginal;    
    private int        posicaoOriginal = -1;
        
    private boolean nova;   
    private boolean podeAlterar;
    private boolean alteracao;
        
    /**
     * Criando nova Anotação
     */
    public TelaAnotacao(AcessoTelaAnotacao acessoTelaAnotacao) {
        
        this.acessoTelaAnotacao = acessoTelaAnotacao;
        this.componente         = acessoTelaAnotacao.getComponente();                
        
        initComponents();        
                
        nova = true;
        podeAlterar = true;
        
        try {
            montarInterface();    
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(acessoTelaAnotacao.getFrame(), "Erro ao recuperar informações da Anotação no Banco de Dados!");
            dispose();
        }

    }
    
    /**
     * Abrindo Anotação a partir da Janela do Componente
     */
    public TelaAnotacao(AcessoTelaAnotacao acessoTelaAnotacao, int posicao) {
        
        this.acessoTelaAnotacao = acessoTelaAnotacao;
        this.componente         = acessoTelaAnotacao.getComponente();                
        
        initComponents();        
                
        podeAlterar      = acessoTelaAnotacao.podeAlterar();
        posicaoOriginal  = posicao;
        anotacaoOriginal = (Anotacao) componente.getAnotacoes().get(posicaoOriginal);   
        
        try {
            montarInterface();    
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(acessoTelaAnotacao.getFrame(), "Erro ao recuperar informações da Anotação no Banco de Dados!");
            dispose();
        }
    }
    
    /**
     * Abrindo Anotação a partir da listagem de todos os componentes heuristicos
     */
    public TelaAnotacao(AcessoTelaAnotacao acessoTelaAnotacao, Anotacao anotacaoOriginal) {
        
        this.acessoTelaAnotacao = acessoTelaAnotacao;        
        this.componente         = anotacaoOriginal.getComponente();        
        
        initComponents();                
        
        podeAlterar           = acessoTelaAnotacao.podeAlterar();
        this.anotacaoOriginal = anotacaoOriginal;
        
        try {
            montarInterface();    
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(acessoTelaAnotacao.getFrame(), "Erro ao recuperar informações da Anotação no Banco de Dados!");
            dispose();
        }
    }
    
    @Override
    public Frame getFrame(){
        return this;        
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaAnotacao;
    } 
    
    private void montarInterface() throws Exception {       
        
        jTextFieldComponente.setText(componente.getNomeTipoComponente() + " - " + componente.getNome());
        
        if (nova){            
            jLabelIcone.setIcon(Anotacoes.retornaIconeAnotacao((Tipo)jComboBoxTipoAnotacao.getSelectedItem()));
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(HeuChess.usuario.getNome()));
            jTextFieldVersao.setText("1");                
            setTitle("Anotação - Nova");
        }else{
            jTextFieldTituloAnotacao.setText(anotacaoOriginal.getNome());
            jTextAreaTextoAnotacao.setText(anotacaoOriginal.getInformacao());
            jLabelIcone.setIcon(Anotacoes.retornaIconeAnotacao(anotacaoOriginal.getTipo()));        
            jComboBoxTipoAnotacao.setSelectedItem(anotacaoOriginal.getTipo());
            jTextFieldVersao.setText(""+anotacaoOriginal.getVersao());
            jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(anotacaoOriginal.getDataCriacao()));
            jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(anotacaoOriginal.getDataUltimaModificacao()));
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(anotacaoOriginal.getIdAutor())));
            atualizaTotalCaracteres();
            setTitle("Anotação - " + jTextFieldTituloAnotacao.getText());
            
            Historico.registraComponenteAberto(anotacaoOriginal);
            ConexaoDBHeuChess.commit();
        }        
                
        alteracao = false;
                
        if (!podeAlterar){
            jTextFieldTituloAnotacao.setEditable(false);
            jComboBoxTipoAnotacao.setEnabled(false);
            jTextAreaTextoAnotacao.setEditable(false);
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
                
        ModalFrameUtil.showAsModalDontBlock(this); 
        jTextFieldTituloAnotacao.requestFocus();
    }
   
    private void atualizaTotalCaracteres(){
        
        String texto = jTextAreaTextoAnotacao.getText();
        if (texto != null){
            int totalTeclado = texto.length();
            jLabelTotalCaracteres.setText("Total de Caracteres = " + totalTeclado);
        }
    }

    private void confirmaCancelar(){
        
        if (podeAlterar) {
            
            if (alteracao) {
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?",
                                                                 "Confirmação Cancelamento");
                
                if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                    return;
                }
            }
        }
        
        if (posicaoOriginal != -1) {
            Anotacoes.seleciona(acessoTelaAnotacao, posicaoOriginal);
        }
        
        dispose();
        acessoTelaAnotacao.fechandoTelaAnotacao(false);
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null){
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(usuario.getNome()));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCentral = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldTituloAnotacao = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jComboBoxTipoAnotacao = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jLabelTotalCaracteres = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaTextoAnotacao = new javax.swing.JTextArea();
        jLabelIcone = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldComponente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldVersao = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabelDataModificacao = new javax.swing.JLabel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jButtonDadosAutor = new javax.swing.JButton();
        jPanelBase = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Anotação");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_anotacao.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelCentral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setText("Título ");

        jTextFieldTituloAnotacao.setText("Explicação Inicial");
        jTextFieldTituloAnotacao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldTituloAnotacaoKeyReleased(evt);
            }
        });

        jLabel16.setText("Tipo");

        for (Tipo tipo : AnotacaoDAO.tiposAnotacao){
            jComboBoxTipoAnotacao.addItem(tipo);
        }
        jComboBoxTipoAnotacao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxTipoAnotacao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTipoAnotacaoItemStateChanged(evt);
            }
        });

        jLabel17.setText("Descrição");

        jLabelTotalCaracteres.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotalCaracteres.setText("Total de Caracteres = 0");

        jTextAreaTextoAnotacao.setColumns(20);
        jTextAreaTextoAnotacao.setLineWrap(true);
        jTextAreaTextoAnotacao.setRows(5);
        jTextAreaTextoAnotacao.setWrapStyleWord(true);
        jTextAreaTextoAnotacao.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jTextAreaTextoAnotacao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaTextoAnotacaoKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTextAreaTextoAnotacao);

        jLabelIcone.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelIcone.setIconTextGap(0);

        jLabel2.setText("Componente");

        jTextFieldComponente.setEditable(false);

        jLabel3.setText("Versão");

        jTextFieldVersao.setEditable(false);
        jTextFieldVersao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabelDataModificacao.setText("Modificação");

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setText("Autor");

        jTextFieldNomeAutor.setEditable(false);

        jButtonDadosAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_dados_autor.png"))); // NOI18N
        jButtonDadosAutor.setText("Dados do Autor");
        jButtonDadosAutor.setToolTipText("Mostra mais informações sobre o Autor");
        jButtonDadosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDadosAutorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentralLayout = new javax.swing.GroupLayout(jPanelCentral);
        jPanelCentral.setLayout(jPanelCentralLayout);
        jPanelCentralLayout.setHorizontalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelCentralLayout.createSequentialGroup()
                                .addComponent(jTextFieldNomeAutor)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonDadosAutor))
                            .addGroup(jPanelCentralLayout.createSequentialGroup()
                                .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelDataModificacao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 149, Short.MAX_VALUE))
                            .addGroup(jPanelCentralLayout.createSequentialGroup()
                                .addComponent(jTextFieldTituloAnotacao)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxTipoAnotacao, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldComponente)))
                    .addComponent(jLabelTotalCaracteres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addComponent(jLabelIcone, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanelCentralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextFieldDataCriacao, jTextFieldDataModificacao, jTextFieldVersao});

        jPanelCentralLayout.setVerticalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldComponente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jButtonDadosAutor)
                    .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jComboBoxTipoAnotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jTextFieldTituloAnotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDataModificacao)
                    .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(jLabelIcone, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalCaracteres)
                .addContainerGap())
        );

        jPanelCentralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jTextFieldDataCriacao, jTextFieldDataModificacao, jTextFieldVersao});

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setMnemonic('C');
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
            }
        });

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('n');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBaseLayout = new javax.swing.GroupLayout(jPanelBase);
        jPanelBase.setLayout(jPanelBaseLayout);
        jPanelBaseLayout.setHorizontalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jPanelBaseLayout.setVerticalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addGroup(jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConfirmar)
                    .addComponent(jButtonAjuda)
                    .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-680)/2, (screenSize.height-458)/2, 680, 458);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextAreaTextoAnotacaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaTextoAnotacaoKeyReleased
        alteracao = true;
        atualizaTotalCaracteres();
    }//GEN-LAST:event_jTextAreaTextoAnotacaoKeyReleased

    private void jTextFieldTituloAnotacaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTituloAnotacaoKeyReleased
        alteracao = true;
        setTitle("Anotação - " + jTextFieldTituloAnotacao.getText());
    }//GEN-LAST:event_jTextFieldTituloAnotacaoKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed
        
        if (alteracao || nova){
            
            String titulo    = jTextFieldTituloAnotacao.getText();
            String descricao = jTextAreaTextoAnotacao.getText();
            
            if (titulo == null || titulo.trim().length() == 0){
                UtilsGUI.dialogoErro(this, "O título da Anotação não esta preenchido.\n" +
                                           "Uma anotação precisa ter um título definido para poder ser salva!");
                return;            
            }
            
            if (descricao == null || descricao.trim().length() == 0){
                UtilsGUI.dialogoErro(this, "A descrição da Anotação Inicial não foi preenchido.\n" +
                                           "Uma anotação precisa ter um texto de descrição para poder ser salva!");
                return;            
            }
            
            if (descricao.indexOf("'") != -1){/////////////////////////////////////////////////////////GAMBIARRA/////////////////////////
                descricao = descricao.replaceAll("'","\"");        
            }
                    
            if (nova) {            
                Anotacao novaAnotacao = new Anotacao(HeuChess.usuario.getId(),
                                                     componente,
                                                     (Tipo) jComboBoxTipoAnotacao.getSelectedItem(),
                                                     titulo,
                                                     descricao);
                try{
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    AnotacaoDAO.adiciona(novaAnotacao);
                
                    ConexaoDBHeuChess.commit();
                    
                    ModelListaComponentes model = (ModelListaComponentes) acessoTelaAnotacao.getJListAnotacoes().getModel();
                    model.add(0, novaAnotacao);                
                                        
                    acessoTelaAnotacao.atualizaVersaoDataUltimaModificacao();
                    Anotacoes.atualizaQuantidadeAnotacoes(acessoTelaAnotacao);
                    Anotacoes.seleciona(acessoTelaAnotacao, 0);                                   
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                     
                }catch(Exception e){
                    HeuChess.desfazTransacao(e);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao tentar criar a Anotacao no Banco de Dados\nOperação Cancelada!");                    
                    return;                
                }
                
            }else{
                ///////////////////////////////////
                // Backup dos valores anteriores //
                ///////////////////////////////////
                
                String tituloOriginal       = anotacaoOriginal.getNome();
                String descricaoOriginal    = anotacaoOriginal.getInformacao();
                Tipo   tipoAnotacaoOriginal = anotacaoOriginal.getTipo();
                
                anotacaoOriginal.setNome(titulo);
                anotacaoOriginal.setInformacao(descricao);
                anotacaoOriginal.setTipo((Tipo) jComboBoxTipoAnotacao.getSelectedItem());
            
                try{
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    AnotacaoDAO.atualiza(anotacaoOriginal);
                
                    ConexaoDBHeuChess.commit();
                    
                    if (posicaoOriginal != -1){
                        
                        ////////////////////////////////////////////////
                        // Foi Aberto através da Janela do Componente //
                        ////////////////////////////////////////////////
                        
                        ModelListaComponentes model = (ModelListaComponentes) acessoTelaAnotacao.getJListAnotacoes().getModel();                        
                        
                        model.set(posicaoOriginal, anotacaoOriginal);
                        
                        acessoTelaAnotacao.atualizaVersaoDataUltimaModificacao();
                        Anotacoes.seleciona(acessoTelaAnotacao, posicaoOriginal);                                   
                                                
                    }else{
                        ////////////////////////////////////////////////////////
                        // Foi Aberto através da Tree Componentes Heurísticos //
                        ////////////////////////////////////////////////////////
                        
                        int posicao = componente.getAnotacoes().indexOf(anotacaoOriginal);
                        
                        componente.getAnotacoes().set(posicao, anotacaoOriginal);
                    }
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                } catch (Exception e) {
                    HeuChess.desfazTransacao(e);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar a Anotacao no Banco de Dados\nOperação Cancelada!");

                    // Voltando valores originais //

                    anotacaoOriginal.setNome(tituloOriginal);
                    anotacaoOriginal.setInformacao(descricaoOriginal);
                    anotacaoOriginal.setTipo(tipoAnotacaoOriginal);
                    return;
                }
            }    
            
            dispose();            
            acessoTelaAnotacao.fechandoTelaAnotacao(true);
            
        }else{
            
            if (posicaoOriginal != -1){
                Anotacoes.seleciona(acessoTelaAnotacao, posicaoOriginal);                
            }
            
            dispose();
            acessoTelaAnotacao.fechandoTelaAnotacao(false);
        }
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jComboBoxTipoAnotacaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTipoAnotacaoItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED){
            alteracao = true;        
            jLabelIcone.setIcon(Anotacoes.retornaIconeAnotacao((Tipo) jComboBoxTipoAnotacao.getSelectedItem()));        
        }
    }//GEN-LAST:event_jComboBoxTipoAnotacaoItemStateChanged

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaAnotacao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        
        if (nova){
            HeuChess.dadosAutor(this, HeuChess.usuario.getId());
        }else{
            HeuChess.dadosAutor(this, anotacaoOriginal.getIdAutor());
        }        
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JComboBox jComboBoxTipoAnotacao;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelDataModificacao;
    private javax.swing.JLabel jLabelIcone;
    private javax.swing.JLabel jLabelTotalCaracteres;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaTextoAnotacao;
    private javax.swing.JTextField jTextFieldComponente;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldTituloAnotacao;
    private javax.swing.JTextField jTextFieldVersao;
    // End of variables declaration//GEN-END:variables
}
