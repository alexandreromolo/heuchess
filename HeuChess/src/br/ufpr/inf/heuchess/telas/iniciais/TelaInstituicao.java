package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.InstituicaoDAO;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.organizacao.Instituicao;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 17 de Julho de 2006, 10:56
 */
public class TelaInstituicao extends javax.swing.JFrame implements AcessoTelaLocalizaUsuario {
    
    private Instituicao   instituicao;
    private Instituicao   instituicaoOriginal;    
    private Usuario       usuarioCoordenador;
    
    private TelaPrincipal telaPrincipal;    
    
    private boolean nova;    
    private boolean podeEditar;
    
    /**
     * Criando nova Instituição
     */
    public TelaInstituicao(TelaPrincipal telaPrincipal) {
        
        telaPrincipal.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        this.telaPrincipal = telaPrincipal;
                
        initComponents();        
        
        instituicao = new Instituicao();
        
        nova       = true;
        podeEditar = true;
        
        montarInterface();     
    }
    
    /**
     * Abrindo uma Instituição já existente
     */
    public TelaInstituicao(TelaPrincipal telaPrincipal, Instituicao instituicao) {
        
        this.telaPrincipal  = telaPrincipal;
        instituicaoOriginal = instituicao;
            
        this.instituicao = instituicaoOriginal.geraClone();
        
        try {
            usuarioCoordenador = UsuarioDAO.busca(instituicao.getIdCoordenador());
            
        } catch (Exception ex) {
            HeuChess.registraExcecao(ex);
            
            telaPrincipal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(telaPrincipal,"Erro ao carregar dados do Coordenador da Instituição no Banco!\n" + ex.getMessage());
            dispose();
            return;
        }
        
        // Pode editar os dados de uma instituição caso seja Administrador ou coordenador da instituição.
        
        if ((HeuChess.usuario.getId() == usuarioCoordenador.getId()) || (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR)){
            podeEditar = true;
        }
        
        initComponents();                
        
        montarInterface();
    }
    
    @Override
    public Frame getFrame(){
        return this;        
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return telaPrincipal;
    } 
    
    private void montarInterface(){       
        
        if (nova){                            
            setTitle("Instituição - Nova");
        }else{
            jTextFieldNome.setText(instituicao.getNome());
            jTextAreaTextoDescricao.setText(instituicao.getDescricao());
            jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(instituicao.getDataCriacao()));
            jTextFieldNomeCoordenador.setText(usuarioCoordenador.getNome());
            
            atualizaTotalCaracteres();
            
            setTitle("Instituição - " + jTextFieldNome.getText());            
        }        
                
        if (!podeEditar){
            jTextFieldNome.setEditable(false);
            jTextAreaTextoDescricao.setEditable(false);            
            jButtonLocalizarUsuario.setVisible(false);
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
        
        telaPrincipal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        ModalFrameUtil.showAsModalDontBlock(this); 
        jTextFieldNome.requestFocus();
        jTextFieldNome.setCaretPosition(0);
    }
    
    private void atualizaTotalCaracteres(){
        
        String texto = jTextAreaTextoDescricao.getText();
        if (texto != null){
            int totalTeclado = texto.length();
            jLabelTotalCaracteres.setText("Total de Caracteres = " + totalTeclado);
        }
    }

    private void confirmaCancelar(){
        
        if (podeEditar){
            
            boolean sofreuAlteracao;
        
            if (nova) {
                sofreuAlteracao = true;
            } else {

                try {
                    if (salvarEntrada() != 0) {
                        sofreuAlteracao = true;
                    } else {
                        sofreuAlteracao = !instituicao.igual(instituicaoOriginal);
                    }
                } catch (Exception e) {
                    HeuChess.registraExcecao(e);
                    sofreuAlteracao = true;
                }
            }
        
            if (sofreuAlteracao){
                
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?",
                                                                 "Confirmação Cancelamento");
                
                if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                    return;
                }
            }
        }
        
        dispose();
        telaPrincipal.fechandoTelaInstituicao(null,false);
    }
    
    private int salvarEntrada() throws Exception {

        String nome            = jTextFieldNome.getText();
        String descricao       = jTextAreaTextoDescricao.getText();
        String nomeCoordenador = jTextFieldNomeCoordenador.getText();

        if (nome == null || nome.trim().length() == 0) {
            return -1;
        }

        if (descricao == null || descricao.trim().length() == 0) {
            return -2;
        }

        if (nomeCoordenador == null || nomeCoordenador.trim().length() == 0) {
            return -3;
        }

        nome = UtilsString.preparaStringParaBD(jTextFieldNome.getText(), true, Formato.TUDO_MAIUSCULO);

        if (nova || !nome.equalsIgnoreCase(instituicaoOriginal.getNome())){
            
            long idAchado = InstituicaoDAO.existeNome(nome);
        
            if (idAchado != -1 && instituicao.getId() != idAchado){            
                return -4;
            }  
        }
        
        instituicao.setNome(nome);            
        instituicao.setDescricao(descricao);
        instituicao.setIdCoordenador(usuarioCoordenador.getId());

        return 0;
    }
    
    @Override
    public void fechandoTelaLocalizaUsuario(Usuario usuario) {
        
        if (usuario != null){
            usuarioCoordenador = usuario;
            jTextFieldNomeCoordenador.setText(usuarioCoordenador.getNome());
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
        jTextFieldNome = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabelTotalCaracteres = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaTextoDescricao = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNomeCoordenador = new javax.swing.JTextField();
        jButtonLocalizarUsuario = new javax.swing.JButton();
        jPanelBase = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Instituição");
        setIconImage(new ImageIcon(getClass().getResource("/icones/casa.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelCentral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setText("Nome");

        jTextFieldNome.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeKeyReleased(evt);
            }
        });

        jLabel17.setText("Descrição");

        jLabelTotalCaracteres.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotalCaracteres.setText("Total de Caracteres = 0");

        jTextAreaTextoDescricao.setColumns(20);
        jTextAreaTextoDescricao.setLineWrap(true);
        jTextAreaTextoDescricao.setRows(5);
        jTextAreaTextoDescricao.setWrapStyleWord(true);
        jTextAreaTextoDescricao.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jTextAreaTextoDescricao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaTextoDescricaoKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTextAreaTextoDescricao);

        jLabel4.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel1.setText("Coordenador");

        jTextFieldNomeCoordenador.setEditable(false);

        jButtonLocalizarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/pesquisar.png"))); // NOI18N
        jButtonLocalizarUsuario.setMnemonic('l');
        jButtonLocalizarUsuario.setText("Localizar");
        jButtonLocalizarUsuario.setToolTipText("");
        jButtonLocalizarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocalizarUsuarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentralLayout = new javax.swing.GroupLayout(jPanelCentral);
        jPanelCentral.setLayout(jPanelCentralLayout);
        jPanelCentralLayout.setHorizontalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelTotalCaracteres, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldNome, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                            .addComponent(jTextFieldNomeCoordenador))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentralLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonLocalizarUsuario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelCentralLayout.setVerticalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldNomeCoordenador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLocalizarUsuario))
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalCaracteres)
                .addContainerGap())
        );

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
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
                .addContainerGap()
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
        setBounds((screenSize.width-565)/2, (screenSize.height-368)/2, 565, 368);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextAreaTextoDescricaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaTextoDescricaoKeyReleased
        atualizaTotalCaracteres();
    }//GEN-LAST:event_jTextAreaTextoDescricaoKeyReleased

    private void jTextFieldNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeKeyReleased
        setTitle("Instituição - " + jTextFieldNome.getText());
    }//GEN-LAST:event_jTextFieldNomeKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed

        try {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            int erro = salvarEntrada();

            switch (erro) {

                case 0:// Nenhum erro //
                    break;

                case -1:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "O nome da Instituição não esta preenchido.\n" +
                                               "Uma Instituição precisa ter um nome definido para poder ser salva!");
                    
                    jTextFieldNome.requestFocus();
                    return;

                case -2:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A descrição da Instituição não foi preenchida.\n" +
                                               "Uma Instituição precisa ter um texto de descrição para poder ser salva!");
                    
                    jTextAreaTextoDescricao.requestFocus();
                    return;

                case -3:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "O Coordenador da Instituição não foi escolhido.\n" +
                                               "Uma Instituição precisa ter um coordenador definido para poder ser salva!");
                    
                    jButtonLocalizarUsuario.requestFocus();
                    return;

                case -4:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Você já possui uma Instituição com este nome!.\nEscolha outro.");
                    
                    jTextFieldNome.requestFocus();
                    jTextFieldNome.selectAll();
                    return;

                default:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Código de erro não tratado pela aplicação [" + erro + "]");
                    
                    IllegalArgumentException excecao = new IllegalArgumentException("Código de erro não tratado pela aplicação [" + erro + "]");
                    HeuChess.registraExcecao(excecao);
                    throw excecao;
            }
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao verificar os valores editados da Instituição!\nOperação Cancelada!");
            return;
        }
        
        if (nova) {
                   
            try{
                InstituicaoDAO.adiciona(instituicao);
                
                ConexaoDBHeuChess.commit();
            
                dispose();
                telaPrincipal.fechandoTelaInstituicao(instituicao, true); 
            
            }catch(Exception e){
                HeuChess.desfazTransacao(e);
                    
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar criar a Instituição no Banco de Dados\nOperação Cancelada!");
            }
        } else             
            if (!instituicao.igual(instituicaoOriginal)) {
                
                try{
                    InstituicaoDAO.atualiza(instituicao,instituicaoOriginal.getIdCoordenador());
                            
                    ConexaoDBHeuChess.commit();
                
                    dispose();
                    telaPrincipal.fechandoTelaInstituicao(instituicao, false); 
                
                }catch(Exception e){
                    HeuChess.desfazTransacao(e);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar a Instituição no Banco de Dados\nOperação Cancelada!");
                }
            }else{
                dispose();
                telaPrincipal.fechandoTelaInstituicao(null,false);                 
            }        
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaInstituicao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonLocalizarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLocalizarUsuarioActionPerformed
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaLocalizaUsuario tela = new TelaLocalizaUsuario(TelaInstituicao.this);
            }
        });
    }//GEN-LAST:event_jButtonLocalizarUsuarioActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonLocalizarUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTotalCaracteres;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaTextoDescricao;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldNome;
    private javax.swing.JTextField jTextFieldNomeCoordenador;
    // End of variables declaration//GEN-END:variables
}
