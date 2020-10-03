package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.utils.UtilsString;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 20 de Julho de 2006, 12:08
 */
public class RenderTreeFuncoes extends DefaultTreeCellRenderer implements TreeCellRenderer{
    
    public RenderTreeFuncoes() {
        setLeafIcon(new ImageIcon(getClass().getResource("/icones/icone_funcao.png")));            
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        
        if (node.getUserObject() instanceof Funcao){
            
            Funcao funcao = (Funcao) node.getUserObject();
            
            setText(funcao.getNomeCurto());
            //setToolTipText(funcao.getDescricaoFuncao());
            
        }else
            if (node.getUserObject() instanceof Tipo){
                
                Tipo tipo = (Tipo) node.getUserObject();
               
                String texto = tipo.getNome();
                texto = texto.substring(texto.lastIndexOf(' '),texto.length());    
                
                setText(UtilsString.formataCaixaAltaBaixa(texto));
                //setToolTipText(null);
            }
        
        return this;
    }
}