package br.ufpr.inf.utils.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since May 25, 2012
 */
public class UtilsTree {

    /**
     * If expand is true, expands all nodes in the tree. Otherwise, collapses
     * all nodes in the tree.
     */
    public static void expandAll(JTree tree, boolean expand) {
        
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        
        // Traverse children
        
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        
        // Expansion or collapse must be done bottom-up
        
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    public static void addNodeInSortedOrder(DefaultMutableTreeNode parent,DefaultMutableTreeNode child) {
        
        int n = parent.getChildCount();
        if (n == 0) {
            parent.add(child);
            return;
        }
        
        DefaultMutableTreeNode node;
        
        for (int i = 0; i < n; i++) {
            
            node = (DefaultMutableTreeNode) parent.getChildAt(i);
            
            if (node.toString().compareTo(child.toString()) > 0) {
                parent.insert(child, i);
                return;
            }
        }
        parent.add(child);
    }
     
    public static void insertNodeSort(DefaultTreeModel model, DefaultMutableTreeNode node, DefaultMutableTreeNode nodeParent) {

        // obtem o parent do node  

        int posicao = 0;
        int contaFilhos = nodeParent.getChildCount();
        String nodeStr = node.toString();

        // percorre todo o nodo  

        for (int i = 0; i < contaFilhos; i++) {
            
            String nodeParentStr = nodeParent.getChildAt(i).toString();
            
            if (nodeStr.compareToIgnoreCase(nodeParentStr) > 0) {
                posicao++;
            }
        }
        // insere nodo na posicao, os abaixo sao re-organizados automaticamente  

        model.insertNodeInto(node, nodeParent, posicao);
    }
    
    public static DefaultMutableTreeNode searchNode(DefaultMutableTreeNode rootNode, Object object) {
        
        DefaultMutableTreeNode node;
        
        Enumeration e = rootNode.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (object == node.getUserObject()) {
                return node;
            }
        }
        
        return null;
    }
    
    public static DefaultMutableTreeNode searchNodeByName(DefaultMutableTreeNode rootNode, String name) {
        
        DefaultMutableTreeNode node;
        
        Enumeration e = rootNode.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if(name.equals(node.getUserObject().toString())){ 
                return node;
            }
        }
        
        return null;
    }
    
    public static void removeNode(JTree jTree, DefaultMutableTreeNode node){
        
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.removeNodeFromParent(node);    
    }
    
/**
    public static void sortchildren(DefaultMutableTreeNode node) {

        ArrayList children = Collections.list(node.children());
        
        // for getting original location
        
        ArrayList<String> orgCnames = new ArrayList<>();
        
        // new location
        
        ArrayList<String> cNames    = new ArrayList<>();
        
        // move the child to here so we can move them back
        
        DefaultMutableTreeNode temParent = new DefaultMutableTreeNode();
        
        for (Object child : children) {
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode) child;
            temParent.insert(ch, 0);
            cNames.add(ch.toString().toUpperCase());
            orgCnames.add(ch.toString().toUpperCase());
        }
        
        Collections.sort(cNames);
        
        for (String name : cNames) {
            
            // find the original location to get from children arrayList
            
            int indx = orgCnames.indexOf(name);
            node.insert((DefaultMutableTreeNode) children.get(indx), node.getChildCount());
        }
    }
  */  
    
    public static void sortchildren(DefaultTreeModel model, DefaultMutableTreeNode nodeParent) {

        ArrayList children = Collections.list(nodeParent.children());
        
        ArrayList<String> posicaoOriginalNomes = new ArrayList<>();
        ArrayList<String> novaPosicaoNomes     = new ArrayList<>();
        
        for (Object child : children) {
            
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode) child;
            
            posicaoOriginalNomes.add(ch.toString().toUpperCase());
            novaPosicaoNomes.add(ch.toString().toUpperCase());            
        }
        
        Collections.sort(novaPosicaoNomes);
        
        int posAntiga;
        
        for (int x = 0; x < novaPosicaoNomes.size(); x++) {
        
            posAntiga = posicaoOriginalNomes.indexOf(novaPosicaoNomes.get(x));
                    
            if (posAntiga != x){
                
                // Mudou de posição //
                
                DefaultMutableTreeNode nodeMudou = (DefaultMutableTreeNode) children.get(posAntiga); 
                model.removeNodeFromParent(nodeMudou);
                model.insertNodeInto(nodeMudou, nodeParent, x);
            }
        }
    }
    
    public static Enumeration saveExpansionState(JTree tree) {
        return tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
    }
     
    public static void loadExpansionState(JTree tree, Enumeration enumeration) {

        if (enumeration != null) {

            while (enumeration.hasMoreElements()) {

                TreePath treePath = (TreePath) enumeration.nextElement();

                tree.expandPath(treePath);
            }
        }
    }
    
    public static void selecionaTreeNode(JTree jTree, DefaultMutableTreeNode treeNode){
        
        TreePath path = new TreePath(treeNode.getPath());
                
        jTree.setSelectionPath(path);
       
        int row = jTree.getRowForPath(path);
        jTree.scrollRowToVisible(row);                        
        
        //jTree.scrollPathToVisible(path);       
        //jTree.scrollRectToVisible(jTree.getPathBounds(path));        
    }
}

 