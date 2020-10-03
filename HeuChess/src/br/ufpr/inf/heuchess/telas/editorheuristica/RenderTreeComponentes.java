/*
 * RenderTreeFuncoes.java
 *
 * Created on 20 de Julho de 2006, 12:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Anotacao;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Etapa;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Heuristica;
import br.ufpr.inf.heuchess.representacao.heuristica.Regiao;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class RenderTreeComponentes extends DefaultTreeCellRenderer implements TreeCellRenderer{
    
    ImageIcon iconeConjuntoHeuristico = new ImageIcon(getClass().getResource("/icones/icone_conjunto_heuristico.png"));                    
    ImageIcon iconeEtapa              = new ImageIcon(getClass().getResource("/icones/icone_etapa.png"));                    
    ImageIcon iconeHeuristica         = new ImageIcon(getClass().getResource("/icones/icone_heuristica.png"));                    
    ImageIcon iconeRegiao             = new ImageIcon(getClass().getResource("/icones/retangulo.png"));                    
    ImageIcon iconeAnotacao           = new ImageIcon(getClass().getResource("/icones/icone_anotacao.png"));                    
    ImageIcon iconeFuncao             = new ImageIcon(getClass().getResource("/icones/icone_funcao.png"));                    
    
    public RenderTreeComponentes() {
        
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof ConjuntoHeuristico){
            setIcon(iconeConjuntoHeuristico);
            setText(getText() + " (Conjunto Heurístico)");
        }else
            if (nodeInfo instanceof Etapa){
                setIcon(iconeEtapa);                
            }else
                if (nodeInfo instanceof Heuristica){
                    setIcon(iconeHeuristica);                    
                }else
                    if (nodeInfo instanceof Regiao){
                        setIcon(iconeRegiao);
                    }else
                        if (nodeInfo instanceof Anotacao){
                            setIcon(iconeAnotacao);
                        }else
                            if (nodeInfo instanceof Funcao){ 
                                setIcon(iconeFuncao);
                            }else{
                                if (getIcon() == getLeafIcon()){
                                    setIcon(getClosedIcon());
                                }
                            }
        return this;
    }
}