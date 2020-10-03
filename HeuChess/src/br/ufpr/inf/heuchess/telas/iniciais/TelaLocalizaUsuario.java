package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 31, 2012
 */
public class TelaLocalizaUsuario extends javax.swing.JFrame implements ModalFrameHierarchy {

    private AcessoTelaLocalizaUsuario acessoTelaLocalizaUsuario;
    
    /**
     * Construtor chamado quando se está procurando qualquer usuário 
     */
    public TelaLocalizaUsuario(AcessoTelaLocalizaUsuario acessoTelaLocalizaUsuario) {
        
        this.acessoTelaLocalizaUsuario = acessoTelaLocalizaUsuario;
        
        initComponents();
        
        ListSelectionModel listSelecionModel = jTableUsuarios.getSelectionModel();
        listSelecionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting()){
                    return;
                }
                ListSelectionModel rowSM = (ListSelectionModel)e.getSource();
                int selectedIndex = rowSM.getMinSelectionIndex();
         
                if (selectedIndex == -1){
                    jButtonConfirmar.setEnabled(false);
                }else{
                    jButtonConfirmar.setEnabled(true);
                }
            }                
        });
        
        ModalFrameUtil.showAsModalDontBlock(this); 
        jTextFieldTextoPesquisa.requestFocus();
    }

    @Override
    public Frame getFrame() {
        return this;
    }

    @Override
    public ModalFrameHierarchy getModalOwner() {
        return acessoTelaLocalizaUsuario;
    }
    
    private void confirmaCancelar(){
        
        Usuario usuario = recuperaUsuarioSelecionado();
        
        boolean sofreuAlteracao = (usuario == null ? false : true);
                        
        if (sofreuAlteracao){
            int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente cancelar as alterações feitas?","Confirmação Cancelamento");
            if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                return;
            }
        }        
        
        dispose();
        acessoTelaLocalizaUsuario.fechandoTelaLocalizaUsuario(null);
    }
    
    private void realizarPesquisa(){
        
        UsuarioDAO.BuscaSexo sexo;
        
        if (jRadioButtonSexoMasculino.isSelected()){
            sexo = UsuarioDAO.BuscaSexo.MASCULINO;
        }else
            if (jRadioButtonSexoFeminino.isSelected()){
                sexo = UsuarioDAO.BuscaSexo.FEMININO;    
            }else{
                sexo = UsuarioDAO.BuscaSexo.AMBOS;        
            }
        
        String palavra = jTextFieldTextoPesquisa.getText();
        
        if (palavra != null){
            palavra = UtilsString.preparaStringParaBD(palavra, true, UtilsString.Formato.TUDO_MAIUSCULO);
        }
        
        try{
            ArrayList<Usuario> usuarios = UsuarioDAO.lista(palavra, sexo, null, null);
            
            if (usuarios == null || usuarios.isEmpty()){
                UtilsGUI.dialogoAtencao(this,"Nenhum usuário localizado!");
                return;
            }
            
            jTableUsuarios.setModel((TableModel) new TableModelUsuarios(usuarios));
     
            jTableUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
            
            DefaultTableCellRenderer cellRenderEsquerda = new DefaultTableCellRenderer(); 
            cellRenderEsquerda.setHorizontalAlignment(SwingConstants.LEFT); 

            DefaultTableCellRenderer cellRenderCentro = new DefaultTableCellRenderer(); 
            cellRenderCentro.setHorizontalAlignment(SwingConstants.CENTER);
            
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(0)).setCellRenderer(cellRenderCentro);            
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(1)).setCellRenderer(cellRenderEsquerda);
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(2)).setCellRenderer(cellRenderCentro);
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(3)).setCellRenderer(cellRenderCentro);
            
            int largura = jTableUsuarios.getWidth();
            
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(0)).setPreferredWidth((int)(largura * 0.05));  
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(1)).setPreferredWidth((int)(largura * 0.65));  
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(2)).setPreferredWidth((int)(largura * 0.15));  
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(3)).setPreferredWidth((int)(largura * 0.15)); 
            
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(this,"Erro ao realizar pesquisa no Banco de Dados!\n" + e.getMessage());
        }
    }
    
    private Usuario recuperaUsuarioSelecionado(){
        
        int linhaSelecionada = jTableUsuarios.getSelectedRow();
        
        if (linhaSelecionada == -1){
            return null;
        }
       
        TableModelUsuarios usuariosTableModel = (TableModelUsuarios) jTableUsuarios.getModel();
        Usuario usuario = (Usuario) usuariosTableModel.getLinhas().get(linhaSelecionada);
        
        return usuario;
    }
    
    private void confirmaEscolha(){
        
        Usuario usuario = recuperaUsuarioSelecionado();
        
        if (usuario != null){
            dispose();
            acessoTelaLocalizaUsuario.fechandoTelaLocalizaUsuario(usuario);
        }else{
            jButtonConfirmar.setEnabled(false);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupSexo = new javax.swing.ButtonGroup();
        jPanelBase = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jPanelCentral = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldTextoPesquisa = new javax.swing.JTextField();
        jButtonPesquisar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUsuarios = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jRadioButtonSexoMasculino = new javax.swing.JRadioButton();
        jRadioButtonSexoFeminino = new javax.swing.JRadioButton();
        jRadioButtonSexoAmbos = new javax.swing.JRadioButton();
        jButtonLimpar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Localizar Usuário");
        setIconImage(new ImageIcon(getClass().getResource("/icones/pesquisar.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.setMaximumSize(new java.awt.Dimension(101, 25));
        jButtonAjuda.setMinimumSize(new java.awt.Dimension(101, 25));
        jButtonAjuda.setPreferredSize(new java.awt.Dimension(101, 25));
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setMnemonic('C');
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.setEnabled(false);
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
            }
        });

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('n');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.setMaximumSize(new java.awt.Dimension(101, 25));
        jButtonCancelar.setMinimumSize(new java.awt.Dimension(101, 25));
        jButtonCancelar.setPreferredSize(new java.awt.Dimension(101, 25));
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
                .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jPanelBaseLayout.setVerticalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addGroup(jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConfirmar)
                    .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jPanelCentral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nome");

        jTextFieldTextoPesquisa.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER,DocumentMasked.ONLY_CAPITAL));
        jTextFieldTextoPesquisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTextoPesquisaActionPerformed(evt);
            }
        });

        jButtonPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/pesquisar.png"))); // NOI18N
        jButtonPesquisar.setMnemonic('p');
        jButtonPesquisar.setText("Pesquisar");
        jButtonPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPesquisarActionPerformed(evt);
            }
        });

        jTableUsuarios.setCellSelectionEnabled(false);
        jTableUsuarios.setRowSelectionAllowed(true);
        jTableUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableUsuarios);
        jTableUsuarios.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel2.setText("Sexo");

        buttonGroupSexo.add(jRadioButtonSexoMasculino);
        jRadioButtonSexoMasculino.setText("Masculino");

        buttonGroupSexo.add(jRadioButtonSexoFeminino);
        jRadioButtonSexoFeminino.setText("Feminino");

        buttonGroupSexo.add(jRadioButtonSexoAmbos);
        jRadioButtonSexoAmbos.setSelected(true);
        jRadioButtonSexoAmbos.setText("Ambos");

        jButtonLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/borracha.png"))); // NOI18N
        jButtonLimpar.setMnemonic('l');
        jButtonLimpar.setText("Limpar");
        jButtonLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentralLayout = new javax.swing.GroupLayout(jPanelCentral);
        jPanelCentral.setLayout(jPanelCentralLayout);
        jPanelCentralLayout.setHorizontalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonSexoMasculino, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonSexoFeminino, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonSexoAmbos)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldTextoPesquisa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanelCentralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonLimpar, jButtonPesquisar});

        jPanelCentralLayout.setVerticalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldTextoPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPesquisar)
                    .addComponent(jButtonLimpar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jRadioButtonSexoMasculino)
                    .addComponent(jRadioButtonSexoFeminino)
                    .addComponent(jRadioButtonSexoAmbos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelCentralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonLimpar, jButtonPesquisar});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        setBounds((screenSize.width-701)/2, (screenSize.height-419)/2, 701, 419);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaLocalizaUsuario");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed
        confirmaEscolha();        
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPesquisarActionPerformed
        realizarPesquisa();
    }//GEN-LAST:event_jButtonPesquisarActionPerformed

    private void jTextFieldTextoPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTextoPesquisaActionPerformed
        realizarPesquisa();
    }//GEN-LAST:event_jTextFieldTextoPesquisaActionPerformed

    private void jButtonLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimparActionPerformed
        jTextFieldTextoPesquisa.setText(null);
        jTableUsuarios.setModel(new DefaultTableModel());
        jRadioButtonSexoAmbos.setSelected(true);
    }//GEN-LAST:event_jButtonLimparActionPerformed

    private void jTableUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableUsuariosMouseClicked
     
        if (evt.getClickCount() == 2) {
            confirmaEscolha();
        }
    }//GEN-LAST:event_jTableUsuariosMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSexo;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonLimpar;
    private javax.swing.JButton jButtonPesquisar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JRadioButton jRadioButtonSexoAmbos;
    private javax.swing.JRadioButton jRadioButtonSexoFeminino;
    private javax.swing.JRadioButton jRadioButtonSexoMasculino;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableUsuarios;
    private javax.swing.JTextField jTextFieldTextoPesquisa;
    // End of variables declaration//GEN-END:variables
}
