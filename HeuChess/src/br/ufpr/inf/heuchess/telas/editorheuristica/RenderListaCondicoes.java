package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.CondicaoHeuristica;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 25 de Julho de 2006, 16:53
 */
public class RenderListaCondicoes extends DefaultListCellRenderer {
    
    private ArrayList<CondicaoHeuristica> condicoes;    
    
    public RenderListaCondicoes(ArrayList<CondicaoHeuristica> condicoes) {
        
        this.condicoes = condicoes;
        
        setHorizontalAlignment(JLabel.CENTER);        
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        
        setFont(list.getFont());
        
        if (index < condicoes.size()-1){
            
            // Não é o último //            
            
            setText(((CondicaoHeuristica)value).toDHJOG(true));
        }else{
            
            // É o último //
              
            setText(((CondicaoHeuristica)value).toDHJOG(false));
        }
        
        return this;
    }
}