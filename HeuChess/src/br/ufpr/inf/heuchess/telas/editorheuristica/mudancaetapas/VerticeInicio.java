/*
 * VerticeInicio.java
 *
 * Created on 6 de Agosto de 2006, 16:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class VerticeInicio extends DefaultGraphCell {
    
    public VerticeInicio() {
        super("Início");
        
        GPCellViewFactory.setViewClass(getAttributes(),"br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas.JGraphEllipseView");
        
        // Set bounds //
        GraphConstants.setBounds(getAttributes(), new Rectangle2D.Double(50,50,70,50));
        
        // Set fill color //
        GraphConstants.setGradientColor(getAttributes(), Color.BLUE);
        GraphConstants.setOpaque(getAttributes(), true);
        
        GraphConstants.setBorder(getAttributes(), BorderFactory.createRaisedBevelBorder());
        
        // Add a Floating Port
        //addPort();
    }
}
