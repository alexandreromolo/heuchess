package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Aug 13, 2012
 */
public class TelaGerenciaUsuarios extends javax.swing.JFrame implements AcessoTelaUsuario {

    private TelaPrincipal      telaPrincipal;
    private TableModelUsuarios tableModelUsuarios;
    
    public TelaGerenciaUsuarios(TelaPrincipal telaPrincipal) {
        
        this.telaPrincipal = telaPrincipal;
        
        initComponents();
        
        if (HeuChess.usuario.getTipo() != Usuario.ADMINISTRADOR){
            jButtonExcluir.setVisible(false);
        }
        
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
                    jButtonAbrir.setEnabled(false);
                    jButtonExcluir.setEnabled(false);
                }else{
                    jButtonAbrir.setEnabled(true);
                    jButtonExcluir.setEnabled(true);
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
        return telaPrincipal;
    }
    
    private void fechar(){
        dispose();
    }
    
    private void realizarPesquisa(){
        
        jTableUsuarios.setModel(new DefaultTableModel());
        
        UsuarioDAO.BuscaSexo sexo;
        
        if (jComboBoxSexo.getSelectedIndex() == 1){
            sexo = UsuarioDAO.BuscaSexo.MASCULINO;
        }else
            if (jComboBoxSexo.getSelectedIndex() == 2){
                sexo = UsuarioDAO.BuscaSexo.FEMININO;    
            }else{
                sexo = UsuarioDAO.BuscaSexo.AMBOS;        
            }
        
        String palavra = jTextFieldTextoPesquisa.getText();
        
        if (palavra != null){
            palavra = UtilsString.preparaStringParaBD(palavra, true, UtilsString.Formato.TUDO_MAIUSCULO);
        }
        
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            Tipo tipo;
            
            if (jComboBoxTipo.getSelectedIndex() <= 0){
                tipo = null;
            }else{
                tipo = (Tipo) jComboBoxTipo.getSelectedItem();
            }
            
            Situacao situacao;
            
            if (jComboBoxSituacao.getSelectedIndex() <= 0){
                situacao = null;
            }else{
                situacao = (Situacao) jComboBoxSituacao.getSelectedItem();
            }
            
            ArrayList<Usuario> usuarios = UsuarioDAO.lista(palavra, sexo, tipo, situacao);
            
            if (usuarios == null || usuarios.isEmpty()){
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));                
                UtilsGUI.dialogoAtencao(this,"Nenhum usuário localizado!");
                return;
            }
            
            tableModelUsuarios = new TableModelUsuarios(usuarios);

            jTableUsuarios.setModel((TableModel) tableModelUsuarios);

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

            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(0)).setPreferredWidth((int) (largura * 0.05));
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(1)).setPreferredWidth((int) (largura * 0.65));
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(2)).setPreferredWidth((int) (largura * 0.15));
            jTableUsuarios.getColumn(jTableUsuarios.getColumnName(3)).setPreferredWidth((int) (largura * 0.15));
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
    
    private void abrirUsuario(){
        
        final Usuario usuario = recuperaUsuarioSelecionado();
        
        if (usuario != null) {

            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {                    
                    TelaUsuario tela = new TelaUsuario(TelaGerenciaUsuarios.this,usuario);
                }
            });

        } else {
            jButtonAbrir.setEnabled(false);
            jButtonExcluir.setEnabled(false);
        }
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
     
        if (usuario != null) {

            jTextFieldTextoPesquisa.setText(usuario.getNome());

            if (usuario.isSexoMasculino()) {
                jComboBoxSexo.setSelectedIndex(1);                
            } else {
                jComboBoxSexo.setSelectedIndex(2);
            }

            realizarPesquisa();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupSexo = new javax.swing.ButtonGroup();
        jPanelBase = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonFechar = new javax.swing.JButton();
        jPanelBotoesManipulacao = new javax.swing.JPanel();
        jButtonAbrir = new javax.swing.JButton();
        jButtonNovo = new javax.swing.JButton();
        jButtonExcluir = new javax.swing.JButton();
        jPanelCentral = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldTextoPesquisa = new javax.swing.JTextField();
        jButtonPesquisar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUsuarios = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButtonLimpar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxSituacao = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxTipo = new javax.swing.JComboBox();
        jComboBoxSexo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Gerencia Usuários");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_pesquisa_pessoa.png")).getImage());
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

        jButtonFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png"))); // NOI18N
        jButtonFechar.setMnemonic('f');
        jButtonFechar.setText("Fechar");
        jButtonFechar.setMaximumSize(new java.awt.Dimension(101, 25));
        jButtonFechar.setMinimumSize(new java.awt.Dimension(101, 25));
        jButtonFechar.setPreferredSize(new java.awt.Dimension(101, 25));
        jButtonFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharActionPerformed(evt);
            }
        });

        jPanelBotoesManipulacao.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButtonAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrir.setMnemonic('a');
        jButtonAbrir.setText("Abrir");
        jButtonAbrir.setToolTipText("Abre o usuário selecionado");
        jButtonAbrir.setEnabled(false);
        jButtonAbrir.setMaximumSize(new java.awt.Dimension(89, 23));
        jButtonAbrir.setMinimumSize(new java.awt.Dimension(89, 23));
        jButtonAbrir.setPreferredSize(new java.awt.Dimension(89, 23));
        jButtonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirActionPerformed(evt);
            }
        });
        jPanelBotoesManipulacao.add(jButtonAbrir);

        jButtonNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovo.setMnemonic('n');
        jButtonNovo.setText("Novo");
        jButtonNovo.setToolTipText("Cria um novo usuário");
        jButtonNovo.setMaximumSize(new java.awt.Dimension(89, 23));
        jButtonNovo.setMinimumSize(new java.awt.Dimension(89, 23));
        jButtonNovo.setPreferredSize(new java.awt.Dimension(89, 23));
        jButtonNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovoActionPerformed(evt);
            }
        });
        jPanelBotoesManipulacao.add(jButtonNovo);

        jButtonExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluir.setMnemonic('e');
        jButtonExcluir.setText("Excluir");
        jButtonExcluir.setToolTipText("Exclui o usuário selecionado");
        jButtonExcluir.setEnabled(false);
        jButtonExcluir.setMaximumSize(new java.awt.Dimension(89, 23));
        jButtonExcluir.setMinimumSize(new java.awt.Dimension(89, 23));
        jButtonExcluir.setPreferredSize(new java.awt.Dimension(89, 23));
        jButtonExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirActionPerformed(evt);
            }
        });
        jPanelBotoesManipulacao.add(jButtonExcluir);

        javax.swing.GroupLayout jPanelBaseLayout = new javax.swing.GroupLayout(jPanelBase);
        jPanelBase.setLayout(jPanelBaseLayout);
        jPanelBaseLayout.setHorizontalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesManipulacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonFechar});

        jPanelBaseLayout.setVerticalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addGroup(jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelBotoesManipulacao, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonFechar});

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

        jTableUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableUsuarios);
        jTableUsuarios.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel2.setText("Sexo");

        jButtonLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/borracha.png"))); // NOI18N
        jButtonLimpar.setMnemonic('l');
        jButtonLimpar.setText("Limpar");
        jButtonLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimparActionPerformed(evt);
            }
        });

        jLabel3.setText("Situação");

        jComboBoxSituacao.addItem("TODOS");
        jComboBoxSituacao.addItem(Usuario.LIBERADO);
        jComboBoxSituacao.addItem(Usuario.BLOQUEADO);
        jComboBoxSituacao.addItem(Usuario.TROCANDO_SENHA);
        jComboBoxSituacao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        jLabel4.setText("Tipo");

        jComboBoxTipo.addItem("TODOS");
        jComboBoxTipo.addItem(Usuario.APRENDIZ);
        jComboBoxTipo.addItem(Usuario.COORDENADOR);
        jComboBoxTipo.addItem(Usuario.ADMINISTRADOR);
        jComboBoxTipo.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        jComboBoxSexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AMBOS", "MASCULINO", "FEMININO" }));
        jComboBoxSexo.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelCentralLayout = new javax.swing.GroupLayout(jPanelCentral);
        jPanelCentral.setLayout(jPanelCentralLayout);
        jPanelCentralLayout.setHorizontalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelCentralLayout.createSequentialGroup()
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelCentralLayout.createSequentialGroup()
                                .addComponent(jComboBoxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 78, Short.MAX_VALUE))
                            .addGroup(jPanelCentralLayout.createSequentialGroup()
                                .addComponent(jTextFieldTextoPesquisa)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                    .addComponent(jComboBoxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelCentralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonLimpar, jButtonPesquisar});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(jPanelBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-776)/2, (screenSize.height-417)/2, 776, 417);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaGerenciaUsuarios");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirActionPerformed
        abrirUsuario();        
    }//GEN-LAST:event_jButtonAbrirActionPerformed

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharActionPerformed
        fechar();
    }//GEN-LAST:event_jButtonFecharActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        fechar();
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
        jComboBoxSexo.setSelectedIndex(0);
        jComboBoxSituacao.setSelectedIndex(0);
        jComboBoxTipo.setSelectedIndex(0);
    }//GEN-LAST:event_jButtonLimparActionPerformed

    private void jTableUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableUsuariosMouseClicked
     
        if (evt.getClickCount() == 2) {
            abrirUsuario();
        }
    }//GEN-LAST:event_jTableUsuariosMouseClicked

    private void jButtonNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovoActionPerformed

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaUsuario tela = new TelaUsuario(TelaGerenciaUsuarios.this);
            }
        });
    }//GEN-LAST:event_jButtonNovoActionPerformed

    private void jButtonExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirActionPerformed

        Usuario usuario = recuperaUsuarioSelecionado();
        
        if (usuario == null) {
            jButtonAbrir.setEnabled(false);
            jButtonExcluir.setEnabled(false);
            return;
        }
        
        if (HeuChess.usuario.getTipo() != Usuario.ADMINISTRADOR){
            UtilsGUI.dialogoErro(this, "Você não tem permissão para apagar Usuários!");
            return;
        }
        
        if (usuario.getId() == HeuChess.usuario.getId()){
            UtilsGUI.dialogoErro(this, "Você não pode apagar o Usuário que está conetado!");
            return;
        }
        
        if (usuario.getId() == 1) {
            UtilsGUI.dialogoErro(this, "Você não tem permissão para apagar este Usuário!");
            return;
        }

        int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja Realmente Apagar o Usuário\n\"" + usuario.getNome() + "\"?",
                                                         "Confirmação Exclusão");
        if (resposta == JOptionPane.YES_OPTION) {

            try{
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                UsuarioDAO.apaga(usuario.getId());
                
                ConexaoDBHeuChess.commit();
                
                tableModelUsuarios.getLinhas().remove(usuario);
                tableModelUsuarios.update();
            
                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
            }catch(Exception e){
                HeuChess.desfazTransacao(e);
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar apagar o Usuário no Banco de Dados.\nOperação Cancelada!");
            }
        }
    }//GEN-LAST:event_jButtonExcluirActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSexo;
    private javax.swing.JButton jButtonAbrir;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonExcluir;
    private javax.swing.JButton jButtonFechar;
    private javax.swing.JButton jButtonLimpar;
    private javax.swing.JButton jButtonNovo;
    private javax.swing.JButton jButtonPesquisar;
    private javax.swing.JComboBox jComboBoxSexo;
    private javax.swing.JComboBox jComboBoxSituacao;
    private javax.swing.JComboBox jComboBoxTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelBotoesManipulacao;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableUsuarios;
    private javax.swing.JTextField jTextFieldTextoPesquisa;
    // End of variables declaration//GEN-END:variables
}
