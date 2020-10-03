package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.FuncaoDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Frame;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 23 de Julho de 2006, 18:39
 */
public class TelaEscolheFuncao extends javax.swing.JFrame implements ModalFrameHierarchy {
    
    private AcessoTelaEscolheFuncao acessoTelaEscolheFuncao;    
    
    private DHJOG.TipoDado filtroTipoRetorno;
    private Funcao         funcaoOriginal;
    
    public DefaultMutableTreeNode treeFuncoesDisponiveis;
    
    /**
     * Construtor chamado sem ter escolhido nenhuma função previamente
     */
    public TelaEscolheFuncao(AcessoTelaEscolheFuncao acessoTelaEscolheFuncao, DHJOG.TipoDado filtroTipoRetorno) {
        
        this.acessoTelaEscolheFuncao = acessoTelaEscolheFuncao;
        this.filtroTipoRetorno       = filtroTipoRetorno;
        
        if (criaTreeFuncoesDisponiveis() == false){
            UtilsGUI.dialogoErro((JFrame) acessoTelaEscolheFuncao,
                                 "Não existe nenhuma função disponível que retorne " + filtroTipoRetorno + ".\n" +
                                 "Preencha colocando um valor direto.");
            return;
        }
        
        initComponents();
        
        if (filtroTipoRetorno != null){
            jLabelTitulo.setText("Escolha a Função a ser utilizada. São mostradas apenas as que retornam " + filtroTipoRetorno);
        }
        
        ModalFrameUtil.showAsModalDontBlock(this);        
    }
    
    /**
     * Construtor chamado com uma função já foi previamente escolhida. Caso de trocar a função usada
     */
    public TelaEscolheFuncao(AcessoTelaEscolheFuncao acessoTelaEscolheFuncao, DHJOG.TipoDado filtroTipoRetorno, Funcao funcao) {
        
        this(acessoTelaEscolheFuncao,filtroTipoRetorno);
                                     
        ///////////////////////////////////////////////
        // Seleciona a função Passada como Parâmetro //
        ///////////////////////////////////////////////
        
        if (funcao != null){
            
            funcaoOriginal = funcao;
            
            DefaultTreeModel data = (DefaultTreeModel) jTreeFuncoesDisponiveis.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) data.getRoot();
            DefaultMutableTreeNode node = null;
            
            if (root != null){
                
                for (Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements(); ){
                    
                    DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
                    
                    if (funcao.equals(current.getUserObject())){
                        node = current;
                        break;
                    }
                }
            }
            if (node != null){
                
                TreePath path = new TreePath(data.getPathToRoot(node));
                
                jTreeFuncoesDisponiveis.setSelectionPath(path);
                jTreeFuncoesDisponiveis.scrollPathToVisible(path);  
            }
        }else{
            throw new IllegalArgumentException("A função passada vale null");
        }
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaEscolheFuncao;
    }
    
    public final boolean criaTreeFuncoesDisponiveis(){
        
        if (filtroTipoRetorno == null){
            treeFuncoesDisponiveis = HeuChess.treeFuncoesBasicas;
            return true;
        }
        
        treeFuncoesDisponiveis = new DefaultMutableTreeNode("Funções Basicas com tipo de Retorno "+filtroTipoRetorno);
        
        DefaultMutableTreeNode treeFuncoesTempo      = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_TEMPO);
        DefaultMutableTreeNode treeFuncoesPosicao    = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_POSICAO);
        DefaultMutableTreeNode treeFuncoesQuantidade = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_QUANTIDADE);
        DefaultMutableTreeNode treeFuncoesValor      = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_VALOR);
        DefaultMutableTreeNode treeFuncoesSituacao   = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_SITUACAO);
            
        treeFuncoesDisponiveis.add(treeFuncoesTempo);
        treeFuncoesDisponiveis.add(treeFuncoesPosicao);
        treeFuncoesDisponiveis.add(treeFuncoesQuantidade);
        treeFuncoesDisponiveis.add(treeFuncoesValor);
        treeFuncoesDisponiveis.add(treeFuncoesSituacao);            
        
        for (int x = 0; x < FuncaoDAO.funcoesBasicas.size(); x++){
            
            Funcao funcao = FuncaoDAO.funcoesBasicas.get(x);
            
            if (funcao.getTipoRetorno() == filtroTipoRetorno){
                
                if (funcao.getTipo() == Funcao.FUNCAO_BASICA_TEMPO){
                    treeFuncoesTempo.add(new DefaultMutableTreeNode(funcao));
                }else
                    if (funcao.getTipo() == Funcao.FUNCAO_BASICA_POSICAO){
                        treeFuncoesPosicao.add(new DefaultMutableTreeNode(funcao));
                    }else
                        if (funcao.getTipo() == Funcao.FUNCAO_BASICA_QUANTIDADE){
                            treeFuncoesQuantidade.add(new DefaultMutableTreeNode(funcao));
                        }else
                            if (funcao.getTipo() == Funcao.FUNCAO_BASICA_VALOR){
                                treeFuncoesValor.add(new DefaultMutableTreeNode(funcao));
                            }else
                                if (funcao.getTipo() == Funcao.FUNCAO_BASICA_SITUACAO){
                                    treeFuncoesSituacao.add(new DefaultMutableTreeNode(funcao));
                                }else{
                                    throw new IllegalArgumentException("Tipo desconhecido de Função [" + funcao.getTipo() + "]");
                                }
            }
        }
        
        int total = 5;
        
        if (treeFuncoesTempo.getChildCount() == 0){
            treeFuncoesDisponiveis.remove(treeFuncoesTempo);
            total--;
        }
        if (treeFuncoesPosicao.getChildCount() == 0){
            treeFuncoesDisponiveis.remove(treeFuncoesPosicao);
            total--;
        }
        if (treeFuncoesQuantidade.getChildCount() == 0){
            treeFuncoesDisponiveis.remove(treeFuncoesQuantidade);
            total--;
        }
        if (treeFuncoesValor.getChildCount() == 0){
            treeFuncoesDisponiveis.remove(treeFuncoesValor);
            total--;
        }
        if (treeFuncoesSituacao.getChildCount() == 0){
            treeFuncoesDisponiveis.remove(treeFuncoesSituacao);
            total--;
        }        
        return (total > 0 ? true : false);
    }
    
    public void confirmaCancelar(){
        
        boolean escolheuNovaFuncao;
        
        Funcao funcaoEscolhida = recuperaFuncaoEscolhida();
        
        if (funcaoEscolhida == null){
            escolheuNovaFuncao = false;
        }else{
            if (funcaoOriginal == null){
                escolheuNovaFuncao = true;
            }else            
                if (funcaoEscolhida.getId() != funcaoOriginal.getId()){
                    escolheuNovaFuncao = true;
                }else{
                    escolheuNovaFuncao = false;
                }
        }
                
        if (escolheuNovaFuncao){
            int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente cancelar as alterações feitas?","Confirmação Cancelamento");
            if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                return;
            }
        }
        
        dispose();
        acessoTelaEscolheFuncao.fechandoTelaEscolheFuncao(null); 
    }
    
    private Funcao recuperaFuncaoEscolhida(){
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeFuncoesDisponiveis.getLastSelectedPathComponent();

        if (node == null) {        
            return null;
        }

        Object nodeInfo = node.getUserObject();

        if (nodeInfo instanceof Funcao) {
            return (Funcao) nodeInfo;            
        }else{
            return null;
        }
    }
    
    private void confirmandoEscolha() {

        Funcao funcaoEscolhida = recuperaFuncaoEscolhida();
        
        if (funcaoEscolhida == null){
            jButtonConfirmar.setEnabled(false);
            return;
        }
        
        dispose();
        
        if (funcaoOriginal == null){
            acessoTelaEscolheFuncao.fechandoTelaEscolheFuncao(funcaoEscolhida);
        }else
            if (funcaoEscolhida.getId() != funcaoOriginal.getId()){
                acessoTelaEscolheFuncao.fechandoTelaEscolheFuncao(funcaoEscolhida);
            }else{
                acessoTelaEscolheFuncao.fechandoTelaEscolheFuncao(null);
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
        jLabelTitulo = new javax.swing.JLabel();
        jSplitPanePrincipal = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTreeFuncoesDisponiveis = new JTree(treeFuncoesDisponiveis);
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaDescricaoFuncao = new javax.swing.JTextArea();
        jButtonCancelar = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Escolhendo uma Função");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_funcao.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTitulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTitulo.setText("Escolha a Função a ser utilizada");

        jSplitPanePrincipal.setDividerLocation(160);
        jSplitPanePrincipal.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTreeFuncoesDisponiveis.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jTreeFuncoesDisponiveis.setRootVisible(false);
        jTreeFuncoesDisponiveis.setShowsRootHandles(true);
        jTreeFuncoesDisponiveis.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeFuncoesDisponiveis.setCellRenderer(new RenderTreeFuncoes());
        jTreeFuncoesDisponiveis.setToggleClickCount(1);
        jTreeFuncoesDisponiveis.setScrollsOnExpand(true);
        jTreeFuncoesDisponiveis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeFuncoesDisponiveisMouseClicked(evt);
            }
        });
        jTreeFuncoesDisponiveis.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeFuncoesDisponiveisValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTreeFuncoesDisponiveis);

        jSplitPanePrincipal.setLeftComponent(jScrollPane3);

        jTextAreaDescricaoFuncao.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoFuncao.setColumns(20);
        jTextAreaDescricaoFuncao.setEditable(false);
        jTextAreaDescricaoFuncao.setLineWrap(true);
        jTextAreaDescricaoFuncao.setRows(4);
        jTextAreaDescricaoFuncao.setWrapStyleWord(true);
        jTextAreaDescricaoFuncao.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane4.setViewportView(jTextAreaDescricaoFuncao);

        jSplitPanePrincipal.setRightComponent(jScrollPane4);

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPanePrincipal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                    .addComponent(jLabelTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelPrincipalLayout.setVerticalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPanePrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.setEnabled(false);
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                        .addComponent(jButtonConfirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancelar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelar)
                    .addComponent(jButtonConfirmar)
                    .addComponent(jButtonAjuda))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-545)/2, (screenSize.height-393)/2, 545, 393);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed
        confirmandoEscolha();
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();   
    }//GEN-LAST:event_jButtonCancelarActionPerformed
     
    private void jTreeFuncoesDisponiveisValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeFuncoesDisponiveisValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeFuncoesDisponiveis.getLastSelectedPathComponent();
        
        if (node == null){
            jButtonConfirmar.setEnabled(false);
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof Funcao){
            
            Funcao funcao = (Funcao) nodeInfo;
            
            jTextAreaDescricaoFuncao.setText(funcao.getDescricaoFuncao());
            jButtonConfirmar.setEnabled(true);
            
        }else{            
            
            if (nodeInfo instanceof Tipo){
                
                Tipo tipo = (Tipo) nodeInfo;
                jTextAreaDescricaoFuncao.setText(tipo.getDescricao());
            }       
            
            jButtonConfirmar.setEnabled(false);
        }
    }//GEN-LAST:event_jTreeFuncoesDisponiveisValueChanged

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this,"TelaEscolheFuncao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jTreeFuncoesDisponiveisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeFuncoesDisponiveisMouseClicked
        
        if (jTreeFuncoesDisponiveis.isEnabled()){
            
            if (evt.getClickCount() == 2){
                confirmandoEscolha();
            }
        }
    }//GEN-LAST:event_jTreeFuncoesDisponiveisMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPanePrincipal;
    private javax.swing.JTextArea jTextAreaDescricaoFuncao;
    private javax.swing.JTree jTreeFuncoesDisponiveis;
    // End of variables declaration//GEN-END:variables
}
