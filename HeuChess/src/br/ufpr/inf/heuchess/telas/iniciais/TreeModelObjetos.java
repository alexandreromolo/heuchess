package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConjuntoHeuristicoDAO;
import br.ufpr.inf.heuchess.persistencia.InscricaoTurmaDAO;
import br.ufpr.inf.heuchess.persistencia.SituacaoJogoDAO;
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
 * @since  Aug 27, 2012
 */
public class TreeModelObjetos extends ElementoListaTreeModel {

    private DefaultMutableTreeNode treeMinhasTurmas;    
    private DefaultMutableTreeNode treeMeusObjetos;
    private DefaultMutableTreeNode treeMinhasSituacoesJogo;
    private DefaultMutableTreeNode treeMeusConjuntosHeuristicos;
    
    public TreeModelObjetos(JTree jTree) {
        
        super(jTree,"Objetos");
        
        treeMinhasTurmas = new DefaultMutableTreeNode("Meus Colegas de Turma");
        treeNodeRoot.add(treeMinhasTurmas);
        
        treeMeusObjetos = new DefaultMutableTreeNode("Meus Objetos");
        treeNodeRoot.add(treeMeusObjetos);
        
        treeMinhasSituacoesJogo = new DefaultMutableTreeNode("Situações de Jogo");
        treeMeusObjetos.add(treeMinhasSituacoesJogo);
        
        treeMeusConjuntosHeuristicos = new DefaultMutableTreeNode("Conjuntos Heurísticos");
        treeMeusObjetos.add(treeMeusConjuntosHeuristicos);   
    }
    
    public void expandMeusObjetos(){
        jTree.expandPath(new TreePath(treeMeusObjetos.getPath()));        
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

        //////////////////////////////////////
        // Meus Objetos - Situações de Jogo //
        //////////////////////////////////////

        ArrayList<ElementoLista> elementosSituacoesJogo = SituacaoJogoDAO.lista(HeuChess.usuario.getId());

        for (ElementoLista elementoSituacaoJogo : elementosSituacoesJogo) {
            updateElementoLista(elementoSituacaoJogo, treeMinhasSituacoesJogo);
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

        boolean podeAcessar;

        if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR) {
            podeAcessar = true;
        } else {
            int permissoes = Integer.parseInt(elementoTurma.getDescricao());

            if (Permissao.ACESSAR.existe(permissoes)) {
                podeAcessar = true;
            } else {
                podeAcessar = false;
            }
        }
            
        ArrayList<ElementoLista> elementosUsuarios = InscricaoTurmaDAO.listaUsuarios(elementoTurma.getId());
        
        for (ElementoLista elementoUsuario : elementosUsuarios) {

            if (elementoUsuario.getId() != HeuChess.usuario.getId()) {
                
                ElementoListaTreeNode nodeUsuario = updateElementoLista(elementoUsuario, nodeTurma);

                //////////////////////////////////
                // Verificar Objetos do Usuário //
                //////////////////////////////////

                if (podeAcessar) {
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
        DefaultMutableTreeNode nodeSituacoesJogo       = null;
        DefaultMutableTreeNode nodeConjuntosHeuristico = null;
                
        Enumeration e = nodeUsuario.breadthFirstEnumeration();        
        while (e.hasMoreElements()) {

            node = (DefaultMutableTreeNode) e.nextElement();
            
            switch (node.toString()) {
                case "Situações de Jogo":
                    nodeSituacoesJogo = node;
                    break;
                case "Conjuntos Heurísticos":
                    nodeConjuntosHeuristico = node;
                    break;
            }
        }
        
        ///////////////////////
        // Situações de Jogo //
        ///////////////////////

        ArrayList<ElementoLista> elementosSituacoesJogo = SituacaoJogoDAO.lista(elementoUsuario.getId());

        if (!elementosSituacoesJogo.isEmpty()) {

            if (nodeSituacoesJogo == null){
                nodeSituacoesJogo = new DefaultMutableTreeNode("Situações de Jogo");
                insertNodeInto(nodeSituacoesJogo, nodeUsuario, 0);
            }       
            
            for (ElementoLista elementoSituacaoJogo : elementosSituacoesJogo) {
                updateElementoLista(elementoSituacaoJogo, nodeSituacoesJogo);
            }
            
        }else
            if (nodeSituacoesJogo != null){
               nodeUsuario.remove(nodeSituacoesJogo);
               nodeStructureChanged(nodeUsuario);
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