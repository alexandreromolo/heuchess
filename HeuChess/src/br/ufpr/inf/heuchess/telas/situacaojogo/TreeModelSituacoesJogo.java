package br.ufpr.inf.heuchess.telas.situacaojogo;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.InscricaoTurmaDAO;
import br.ufpr.inf.heuchess.persistencia.SituacaoJogoDAO;
import br.ufpr.inf.heuchess.persistencia.TurmaDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
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
public class TreeModelSituacoesJogo extends ElementoListaTreeModel {

    private DefaultMutableTreeNode treeSituacaoJogoPadrao;
    private DefaultMutableTreeNode treeMinhasSituacoesJogo;
    private DefaultMutableTreeNode treeMinhasTurmas;
    
    public final static long COD_TABULEIRO_PADRAO = -1;
    
    public TreeModelSituacoesJogo(JTree jTree) {
        super(jTree,"Situações de Jogo");
        
        treeSituacaoJogoPadrao = new DefaultMutableTreeNode("Padrão");
        treeNodeRoot.add(treeSituacaoJogoPadrao);
        
        ElementoLista tabuleiroPadrao = new ElementoLista(COD_TABULEIRO_PADRAO, 
                                                          "Tabuleiro Inicial", 
                                                          Tabuleiro.TABULEIRO_INICIAL, 
                                                          SituacaoJogo.class,
                                                          SituacaoJogoDAO.tipoIndice(4));
        
        treeSituacaoJogoPadrao.add(new DefaultMutableTreeNode(tabuleiroPadrao));
        
        treeMinhasTurmas = new DefaultMutableTreeNode("Meus Colegas de Turma");
        treeNodeRoot.add(treeMinhasTurmas);
        
        treeMinhasSituacoesJogo = new DefaultMutableTreeNode("Minhas Situações de Jogo");
        treeNodeRoot.add(treeMinhasSituacoesJogo);
    }

    public void expandPadrao(){
        jTree.expandPath(new TreePath(treeSituacaoJogoPadrao.getPath()));        
    }
    
    public void expandMinhasSituacoes(){
        jTree.expandPath(new TreePath(treeMinhasSituacoesJogo.getPath()));        
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
        DefaultMutableTreeNode nodeSituacoesJogo = null;
                
        Enumeration e = nodeUsuario.breadthFirstEnumeration();        
        while (e.hasMoreElements()) {

            node = (DefaultMutableTreeNode) e.nextElement();
            
            if (node.toString().equals("Situações de Jogo")){
                nodeSituacoesJogo = node;
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
    }
}
