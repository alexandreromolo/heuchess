package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.UtilsGUI;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 10:19
 */
public class TelaLogin extends javax.swing.JFrame {
    
    private int quantidadeTentativas;
    
    public TelaLogin() {
        
        //HeuChess.instancia.alteraLookAndFeel("Windows");
        //HeuChess.instancia.atualizaTela(this);
        
        initComponents();
        
        setVisible(true);     
        setExtendedState(JFrame.NORMAL);
        jTextFieldIdentificao.requestFocus();
    }
    
    private void ativarInterface(boolean Ativar){
        
        if (Ativar){
            
              TelaLogin.this.setEnabled(true);              
              jButtonCancelar.setEnabled(true);
              jPasswordFieldSenha.setEnabled(true);              
              jTextFieldIdentificao.setEnabled(true);
              jLabelLogo.setEnabled(true);
              habilitaBotaoConfirmar();
              
        }else{
            
              TelaLogin.this.setEnabled(false);
              jButtonConfirmar.setEnabled(false);
              jButtonCancelar.setEnabled(false);
              jPasswordFieldSenha.setEnabled(false);
              jTextFieldIdentificao.setEnabled(false);
              jLabelLogo.setEnabled(false);
        }        
    }
    
    private void confirmaSaida(){        
        
        ativarInterface(false);  
        
        int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente sair?","Confirmação de Saída");
        
        if (resposta == JOptionPane.YES_OPTION){
            HeuChess.fechaPrograma(0);
        }else{
            ativarInterface(true);
        }        
    }
    
    private void habilitaBotaoConfirmar(){
        
        if (jTextFieldIdentificao.getText().trim().length() > 0){
            
            if (new String(jPasswordFieldSenha.getPassword()).trim().length() > 0){
                jButtonConfirmar.setEnabled(true);
            }else{
                jButtonConfirmar.setEnabled(false);
            }
            
        }else{
            jButtonConfirmar.setEnabled(false);
        }
    }
    
    private void autenticaUsuario(){
        
        if (jTextFieldIdentificao.getText().trim().length() == 0){
            UtilsGUI.dialogoErro(this,"O campo Indentificação está vazio!");
            jTextFieldIdentificao.requestFocus();
            return;
        }
        
        String identificacao = jTextFieldIdentificao.getText().trim();
        
        String senhaDigitada = new String(jPasswordFieldSenha.getPassword());
        
        if (senhaDigitada.length() == 0){            
            UtilsGUI.dialogoErro(this,"O campo Senha está vazio!");
            jPasswordFieldSenha.requestFocus();
            return;
        }
        
        // Procura o Usuário através do Identificador //
        
        Usuario usuario;
        
        try {            
            usuario = UsuarioDAO.busca(identificacao);
            
            if (usuario == null){
                UtilsGUI.dialogoErro(this,"Usuário não foi localizado!\n"+
                                          "Verifique se a Identificação está correta.");
                jTextFieldIdentificao.requestFocus();            
                return;
            }
            
        } catch (Exception ex) {
            HeuChess.avisoFechaPrograma(ex, "Erro ao acessar dados do Usuário no banco de dados!",-2); 
            return;
        }       
        
        // Verifica se usuário está bloqueado
        
        if (usuario.getSituacao() == Usuario.BLOQUEADO){
            UtilsGUI.dialogoErro(this,"Esta conta está BLOQUEADA e não pode acessar o Sistema!\nEntre em contato com o seu coordenador para liberá-la.");
            jPasswordFieldSenha.setText(null);
            jTextFieldIdentificao.requestFocus();
            jTextFieldIdentificao.selectAll();
            return;
        }
        
        // Valida senha //
        
        if (!HeuChess.verificaSenhas(senhaDigitada,usuario.getSenha())){
            
            quantidadeTentativas++;
            
            if (quantidadeTentativas < 3){
                UtilsGUI.dialogoErro(this,"A Senha não é válida!");
                jPasswordFieldSenha.setText(null);
                jPasswordFieldSenha.requestFocus();
            }else{                
                UtilsGUI.dialogoErro(this,"A Senha não é válida!\nVocê já errou " + quantidadeTentativas + " vezes.");
                HeuChess.fechaPrograma(-4);
            }
            
            return;
        }
        
        dispose();        
        HeuChess.inicializa(usuario); 
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelDados = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldIdentificao = new javax.swing.JTextField();
        jPasswordFieldSenha = new javax.swing.JPasswordField();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jLabelLogo = new javax.swing.JLabel();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("HeuChess - Autoria de Heurísticas de Xadrez");
        setAlwaysOnTop(true);
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone-principal.gif")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Identificação");

        jLabel2.setText("Senha");

        jTextFieldIdentificao.setToolTipText("Identificação do Usuário");
        jTextFieldIdentificao.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER, DocumentMasked.ONLY_VERY_SMALL));
        jTextFieldIdentificao.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextFieldIdentificaoCaretUpdate(evt);
            }
        });
        jTextFieldIdentificao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIdentificaoActionPerformed(evt);
            }
        });

        jPasswordFieldSenha.setToolTipText("Senha do Usuário");
        jPasswordFieldSenha.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jPasswordFieldSenhaCaretUpdate(evt);
            }
        });
        jPasswordFieldSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordFieldSenhaActionPerformed(evt);
            }
        });
        jPasswordFieldSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordFieldSenhaFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanelDadosLayout = new javax.swing.GroupLayout(jPanelDados);
        jPanelDados.setLayout(jPanelDadosLayout);
        jPanelDadosLayout.setHorizontalGroup(
            jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPasswordFieldSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(jTextFieldIdentificao, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelDadosLayout.setVerticalGroup(
            jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldIdentificao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordFieldSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(26, 26, 26))
        );

        jPanelDadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPasswordFieldSenha, jTextFieldIdentificao});

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setMnemonic('n');
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.setToolTipText("Confirma os dados e conecta-se ao Sistema");
        jButtonConfirmar.setEnabled(false);
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
            }
        });

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('c');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.setToolTipText("Cancela a entrada no sistema");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/robolegal.png"))); // NOI18N
        jLabelLogo.setAlignmentY(0.0F);
        jLabelLogo.setIconTextGap(0);
        jLabelLogo.setMinimumSize(new java.awt.Dimension(144, 130));

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setMnemonic('a');
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.setMaximumSize(new java.awt.Dimension(101, 25));
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonConfirmar)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancelar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonCancelar)
                        .addComponent(jButtonConfirmar))
                    .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-421)/2, (screenSize.height-215)/2, 421, 215);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldIdentificaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIdentificaoActionPerformed
        jPasswordFieldSenha.requestFocus();        
    }//GEN-LAST:event_jTextFieldIdentificaoActionPerformed

    private void jPasswordFieldSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldSenhaActionPerformed
        
        if (jButtonConfirmar.isEnabled()){
            autenticaUsuario();            
        }else{
            jButtonCancelar.requestFocus();
        }
    }//GEN-LAST:event_jPasswordFieldSenhaActionPerformed

    private void jPasswordFieldSenhaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jPasswordFieldSenhaCaretUpdate
        habilitaBotaoConfirmar();
    }//GEN-LAST:event_jPasswordFieldSenhaCaretUpdate

    private void jTextFieldIdentificaoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextFieldIdentificaoCaretUpdate
        habilitaBotaoConfirmar();
    }//GEN-LAST:event_jTextFieldIdentificaoCaretUpdate
    
    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed
        autenticaUsuario();                          
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaSaida();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaSaida();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaLogin");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jPasswordFieldSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordFieldSenhaFocusGained
        HeuChess.verificaTeclaCapsLock(jPasswordFieldSenha);        
    }//GEN-LAST:event_jPasswordFieldSenhaFocusGained
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JPanel jPanelDados;
    private javax.swing.JPasswordField jPasswordFieldSenha;
    private javax.swing.JTextField jTextFieldIdentificao;
    // End of variables declaration//GEN-END:variables
}
