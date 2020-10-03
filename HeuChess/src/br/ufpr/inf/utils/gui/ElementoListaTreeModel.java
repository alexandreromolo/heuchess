package br.ufpr.inf.utils.gui;

import br.ufpr.inf.heuchess.HeuChess;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Aug 28, 2012
 */
public abstract class ElementoListaTreeModel extends DefaultTreeModel implements Runnable {

    protected JTree                  jTree;
    protected DefaultMutableTreeNode treeNodeRoot;
    protected String                 nome;
    protected Enumeration<TreePath>  expands;
    
    private Thread  thread;
    private boolean executando;           
    private long    tempoAtualizacao;

    /**
     * Construtor com tempo de atualização padrão
     */
    public ElementoListaTreeModel(JTree jTree, String nome) {
        this(jTree,nome,1000);        
    }
    
    /**
     * Construtor que permite configurar um tempo de atualização específico
     */
    public ElementoListaTreeModel(JTree jTree, String nome, long tempoAtualizacao) {
        
        super(new DefaultMutableTreeNode(nome));
        
        if (tempoAtualizacao <= 0){
            throw new IllegalArgumentException("Tempo de atualização não pode ser menor ou igual a zero!");
        }
        
        this.jTree = jTree;
        this.nome  = nome;
        this.tempoAtualizacao = tempoAtualizacao;
              
        treeNodeRoot = (DefaultMutableTreeNode) getRoot();
        
        jTree.setModel(this);
    }
    
    public final DefaultMutableTreeNode getNodeRoot(){
        return treeNodeRoot;
    }
    
    private void zeraChecagem() {
        
        DefaultMutableTreeNode node;
        ElementoListaTreeNode  nodeElemento;
        
        Enumeration e = treeNodeRoot.breadthFirstEnumeration();
        
        while (e.hasMoreElements()) {

            node = (DefaultMutableTreeNode) e.nextElement();

            if (node instanceof ElementoListaTreeNode){
                
                nodeElemento = (ElementoListaTreeNode) node;
                
                nodeElemento.setChecado(false);
                nodeElemento.setExpandido(false);
            }
        }
    }
    
    private void marcaExpandidos() {
        
        if (expands != null) {
            
            DefaultMutableTreeNode node;
            ElementoListaTreeNode nodeElemento;

            while (expands.hasMoreElements()) {

                TreePath path = expands.nextElement();
                
                node = (DefaultMutableTreeNode) path.getLastPathComponent();

                if (node instanceof ElementoListaTreeNode) {

                    nodeElemento = (ElementoListaTreeNode) node;

                    nodeElemento.setExpandido(true);
                }
            }
        }
    }
    
    private void removeNaoChecados() {
        
        DefaultMutableTreeNode node;
        ElementoListaTreeNode  nodeElemento;
        
        Enumeration e = treeNodeRoot.breadthFirstEnumeration();
        
        while (e.hasMoreElements()) {

            node = (DefaultMutableTreeNode) e.nextElement();
          
            if (node instanceof ElementoListaTreeNode) {

                nodeElemento = (ElementoListaTreeNode) node;
                
                if (!nodeElemento.isChecado()) {
                    removeNodeFromParent(node);
                }
            }
        }
    }
    
    protected abstract void update() throws Exception ;
    
    public final ElementoListaTreeNode updateElementoLista(ElementoLista elementoAlvo, DefaultMutableTreeNode nodeParent) throws Exception {

        // Procurar se elementoAlvo já existe na tree //

        ElementoListaTreeNode nodeAchado     = null;
        ElementoLista         elementoAchado = null;

        Enumeration e = nodeParent.breadthFirstEnumeration();
        while (e.hasMoreElements()) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

            if (node instanceof ElementoListaTreeNode) {

                ElementoListaTreeNode nodeElemento = (ElementoListaTreeNode) node;

                elementoAchado = nodeElemento.getElementoLista();

                if ((elementoAchado.getId() == elementoAlvo.getId()) && (elementoAchado.getClasse() == elementoAlvo.getClasse())) {
                    nodeAchado = nodeElemento;
                    break;
                }
            }
        }

        if (nodeAchado != null) {

            // elementoAlvo já existe, verificar atualização //

            nodeAchado.setChecado(true);

            if (!elementoAchado.getNome().equalsIgnoreCase(elementoAlvo.getNome()) || 
                !elementoAchado.getDescricao().equalsIgnoreCase(elementoAlvo.getDescricao()) ||
                 elementoAchado.getTipo() != elementoAlvo.getTipo()) {

                // Atualizou algum dado //

                nodeAchado.setUserObject(elementoAlvo);
                UtilsTree.sortchildren(this, nodeParent);
                nodeChanged(nodeAchado);
            }

        } else {

            // elementoAlvo não existe, incluir novo //

            nodeAchado = new ElementoListaTreeNode(elementoAlvo);
            UtilsTree.insertNodeSort(this, nodeAchado, nodeParent);
        }
        
        return nodeAchado;
    }
    
    public final boolean isExecutando(){
        return executando;
    }
    
    public final void start() {
        
        if (executando){
            throw new IllegalArgumentException("Thread de atualização da Tree \"" + nome + "\" já está ativa!");
        }
        
        try {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception e) {
            HeuChess.registraExcecao(e);
        }
    }
    
    public final void stop(){
        
        if (!executando){
            throw new IllegalArgumentException("Thread de atualização da Tree \"" + nome + "\" não está ativa!");
        }
        
        executando = false;
    }
    
    public final synchronized void forceUpdate() {
        
        try {
            //expands = UtilsTree.saveExpansionState(jTree);

            zeraChecagem();

            //marcaExpandidos();

            update();

            removeNaoChecados();

            //UtilsTree.loadExpansionState(jTree, expands);

        } catch (Exception e) {
            
            //Desabilitado para testes longos de campeonato///////////////////////////////////////////////////////////////////////////////////
            //HeuChess.avisoFechaPrograma(e, "Erro ao atualizar Tree \"" + nome + "\"!", -2);
            
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(null, "Erro ao atualizar Tree \"" + nome + "\"!");
        }
    }
            
    @Override
    public final synchronized void run() {
        
        long tempoInicial, tempoFinal;
        
        executando = true;

        while (executando) {
            
            tempoInicial = System.currentTimeMillis();

            forceUpdate();

            tempoFinal = System.currentTimeMillis();
                
            try {
                if (tempoFinal - tempoInicial < tempoAtualizacao) {
                    wait(tempoAtualizacao - (tempoFinal - tempoInicial));
                }
            } catch (Exception e) {
                HeuChess.registraExcecao(e);
            }
        }
        
        expands = null;
        thread  = null;        
    }
    
    public final boolean selecionaTreeNode(Class classe, long id) {

        DefaultMutableTreeNode node = localizaTreeNode(classe, id);

        if (node != null) {
            
            TreePath path = new TreePath(node.getPath());
            
            jTree.setSelectionPath(path);
            jTree.scrollPathToVisible(path);
            
            return true;
        }else{
            return false;
        }   
    }
    
    public final DefaultMutableTreeNode localizaTreeNode(Class classe, long id) {
        
        DefaultMutableTreeNode node;
        ElementoLista          elemento;
        
        Enumeration e = treeNodeRoot.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            
            node = (DefaultMutableTreeNode) e.nextElement();
            
            if (node.getUserObject() instanceof ElementoLista){
                
                elemento = (ElementoLista) node.getUserObject();
                
                if (elemento.getId() == id && classe == elemento.getClasse()){
                    return node;
                }
            }
        }
        
        return null;
    }
    
    public final boolean verificaDuploClique(MouseEvent evt, Class classe) {
        
        if (jTree.isEnabled()) {

            if (evt.getClickCount() == 2) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

                if (node != null) {
                    
                    Object nodeInfo = node.getUserObject();

                    if (nodeInfo instanceof ElementoLista) {

                        ElementoLista elemento = (ElementoLista) nodeInfo;

                        if (classe == null || elemento.getClasse() == classe) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public final ElementoLista recuperaElementoLista(Class classe) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

        if (node != null) {
            
            Object nodeInfo = node.getUserObject();

            if (nodeInfo instanceof ElementoLista) {

                ElementoLista elemento = (ElementoLista) nodeInfo;

                if (classe == null || elemento.getClasse() == classe) {
                    return elemento;
                }
            }
        }
        
        return null;
    }
}