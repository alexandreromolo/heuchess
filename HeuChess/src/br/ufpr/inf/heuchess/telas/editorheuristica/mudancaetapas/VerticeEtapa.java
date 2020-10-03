package br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas;

import br.ufpr.inf.heuchess.representacao.heuristica.Etapa;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 6 de Agosto de 2006, 16:34
 */
public class VerticeEtapa extends DefaultGraphCell {
    
    public VerticeEtapa(Etapa etapa)  {
        
        super(etapa);
        
        GPCellViewFactory.setViewClass(getAttributes(), "br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas.RoundRectView");
        
        // Set bounds //        
        
        GraphConstants.setBounds(getAttributes(), new Rectangle2D.Double(50,50,100,50));
        
        // Set fill color //
        
        GraphConstants.setGradientColor(getAttributes(), Color.ORANGE);
        GraphConstants.setOpaque(getAttributes(), true);
        
        GraphConstants.setBorder(getAttributes(), BorderFactory.createRaisedBevelBorder());
        
        // Add a Floating Port
        // addPort();        
    }
    
    public Etapa getEtapa(){
        return (Etapa) getUserObject();
    }
}
