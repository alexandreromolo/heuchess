package br.ufpr.inf.heuchess.telas.situacaojogo;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConjuntoHeuristicoDAO;
import br.ufpr.inf.heuchess.persistencia.InscricaoTurmaDAO;
import br.ufpr.inf.heuchess.persistencia.TurmaDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.gui.ElementoLista;
import br.ufpr.inf.utils.gui.ElementoListaTreeModel;
import br.ufpr.inf.utils.gui.ElementoListaTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Aug 29, 2012
 */
public class TreeModelConjuntosHeuristicos extends ElementoListaTreeModel {

    private DefaultMutableTreeNode treeMeusConjuntosHeuristicos;
    private DefaultMutableTreeNode treeMinhasTurmas;
    
    public TreeModelConjuntosHeuristicos(JTree jTree) {
        super(jTree,"Conjuntos Heurísticos");
                
        treeMinhasTurmas = new DefaultMutableTreeNode("Meus Colegas de Turma");
        treeNodeRoot.add(treeMinhasTurmas);
        
        treeMeusConjuntosHeuristicos = new DefaultMutableTreeNode("Meus Conjuntos Heurísticos");
        treeNodeRoot.add(treeMeusConjuntosHeuristicos);
    }

    public void expandMeusConjuntos(){
        jTree.expandPath(new TreePath(treeMeusConjuntosHeuristicos.getPath()));        
    }
    
    @Override
    protected void update() throws Exception {
        
        ///////////////////
        // Minhas Turmas //
        ///////////////////
        
        ArrayList< ElementoLista> elementosTurmas = TurmaDAO.lista(HeuChess.usuario);

        for (ElementoLista elementoTurma : elementosTurmas) {

            ElementoListaTreeNode nodeTurma = updateElementoLista(elementoTurma, treeMinhasTurmas);

            /////////////////////////////////
            // Verificar Usuários da Turma //
            /////////////////////////////////

            updateUsuarios(nodeTurma, elementoTurma);
        }
        
        /////////////////////////////////////////
        // Meus Objetos - Conjunto Heurísticos //
        /////////////////////////////////////////
        
        ArrayList<ElementoLista> elementosConjuntosHeuristicos = ConjuntoHeuristicoDAO.lista(HeuChess.usuario.getId());

        for (ElementoLista elementoConjuntoHeuristico : elementosConjuntosHeuristicos) {
            updateElementoLista(elementoConjuntoHeuristico, treeMeusConjuntosHeuristicos);
        }
    }    
    
    private void updateUsuarios(ElementoListaTreeNode nodeTurma, ElementoLista elementoTurma) throws Exception {

        boolean podeUtilizar;

        if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR){
            podeUtilizar = true;
        }else{
            int permissoes = Integer.parseInt(elementoTurma.getDescricao());
        
            if (Permissao.UTILIZAR.existe(permissoes)) {
                podeUtilizar = true;
            } else {
                podeUtilizar = false;
            }
        }
        
        ArrayList<ElementoLista> elementosUsuarios = InscricaoTurmaDAO.listaUsuarios(elementoTurma.getId());
        
        for (ElementoLista elementoUsuario : elementosUsuarios) {

            if (elementoUsuario.getId() != HeuChess.usuario.getId()) {
                
                ElementoListaTreeNode nodeUsuario = updateElementoLista(elementoUsuario, nodeTurma);

                //////////////////////////////////
                // Verificar Objetos do Usuário //
                //////////////////////////////////

                if (podeUtilizar) {
                    updateObjetosUsuario(nodeUsuario, elementoUsuario);
                } else {
                    if (nodeUsuario.getChildCount() > 0) {

                        // Remover objetos que não podem ser mais vistos //

                        nodeUsuario.removeAllChildren();
                        nodeStructureChanged(nodeUsuario);
                    }
                }
            }
        }
    }
    
    private void updateObjetosUsuario(ElementoListaTreeNode nodeUsuario, ElementoLista elementoUsuario) throws Exception {

        DefaultMutableTreeNode node;
        DefaultMutableTreeNode nodeConjuntosHeuristico = null;
                
        Enumeration e = nodeUsuario.breadthFirstEnumeration();        
        while (e.hasMoreElements()) {

            node = (DefaultMutableTreeNode) e.nextElement();
            
            if (node.toString().equals("Conjuntos Heurísticos")){
                nodeConjuntosHeuristico = node;
                break;
            }
        }
                
        //////////////////////////
        // Conjunto Heurísticos //
        //////////////////////////

        ArrayList<ElementoLista> elementosConjuntosHeuristicos = ConjuntoHeuristicoDAO.lista(elementoUsuario.getId());

        if (!elementosConjuntosHeuristicos.isEmpty()) {

            if (nodeConjuntosHeuristico == null){
                nodeConjuntosHeuristico = new DefaultMutableTreeNode("Conjuntos Heurísticos");
                insertNodeInto(nodeConjuntosHeuristico, nodeUsuario, nodeUsuario.getChildCount());                
            }

            for (ElementoLista elementoConjuntoHeuristico : elementosConjuntosHeuristicos) {
                updateElementoLista(elementoConjuntoHeuristico, nodeConjuntosHeuristico);
            }
            
        }else
            if (nodeConjuntosHeuristico != null){
                nodeUsuario.remove(nodeConjuntosHeuristico);
                nodeStructureChanged(nodeUsuario);
            }
    }
}
