/*
 * ColorIcon.java
 *
 * Created on 4 de Agosto de 2006, 01:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;
import javax.swing.Icon;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class ColorIcon implements Icon {
    
    private Color cor;
    
    public ColorIcon(Color cor) {
        this.cor = cor;        
    }
    
    public int getIconWidth() {
        return 14;
    }
    
    public int getIconHeight() {
        return 14;
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
        g.setColor(cor);
        g.fillRect(x+2, y+2, getIconWidth()-4, getIconHeight()-4);
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }
}
