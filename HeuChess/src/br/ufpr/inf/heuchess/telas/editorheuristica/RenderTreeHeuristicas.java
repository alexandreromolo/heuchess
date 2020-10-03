package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.ExpressaoCalculoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Heuristica;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 26 de Julho de 2006, 17:22
 */
public class RenderTreeHeuristicas extends DefaultTreeCellRenderer implements TreeCellRenderer{
    
    ImageIcon iconeHeuristica       = new ImageIcon(getClass().getResource("/icones/icone_heuristica.png"));
    ImageIcon iconeExpressaoCalculo = new ImageIcon(getClass().getResource("/icones/icone_expressao.png"));
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        
        if (node.getUserObject() instanceof Heuristica){            
            setIcon(iconeHeuristica);            
        }else{
            if (node.getUserObject() instanceof ExpressaoCalculoHeuristico){                
                setIcon(iconeExpressaoCalculo);
            }else{
                setToolTipText(null);            
                if (getIcon() == getLeafIcon()){
                    setIcon(getClosedIcon());
                }
            }
        }        
        return this;
    }
}