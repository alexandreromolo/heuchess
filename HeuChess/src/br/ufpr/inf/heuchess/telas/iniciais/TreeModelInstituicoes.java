package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.InstituicaoDAO;
import br.ufpr.inf.heuchess.persistencia.TurmaDAO;
import br.ufpr.inf.utils.gui.ElementoLista;
import br.ufpr.inf.utils.gui.ElementoListaTreeModel;
import br.ufpr.inf.utils.gui.ElementoListaTreeNode;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Aug 24, 2012
 */
public class TreeModelInstituicoes extends ElementoListaTreeModel {

    public TreeModelInstituicoes(JTree jTree) {
        super(jTree,"Instituições");
    }
    
    public void expandInstituicaoes(){
        jTree.expandPath(new TreePath(getNodeRoot().getPath()));        
    }
    
    @Override
    protected void update() throws Exception {

        ArrayList<ElementoLista> instituicoes = InstituicaoDAO.lista(HeuChess.usuario);
        
        for (ElementoLista elementoInstituicao : instituicoes) {

            ElementoListaTreeNode nodeInstituicao = updateElementoLista(elementoInstituicao, treeNodeRoot);

            ArrayList<ElementoLista> turmas = TurmaDAO.lista(elementoInstituicao.getId());

            for (ElementoLista elementoTurma : turmas) {
                updateElementoLista(elementoTurma, nodeInstituicao);
            }
        }
    }
}
