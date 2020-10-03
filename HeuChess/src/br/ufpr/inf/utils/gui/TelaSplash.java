package br.ufpr.inf.utils.gui;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class TelaSplash {
    
    public static SplashScreen  mySplash;
    public static Rectangle2D   splashTextArea;
    public static Rectangle2D   splashProgressArea;
    public static Graphics2D    splashGraphics;
    public static Font          font;
    public static ImageIcon     fundo;    
    
    public TelaSplash(String nomeArquivo) {
        
        mySplash = SplashScreen.getSplashScreen();
        
        if (mySplash != null) {

            // if there are any problems displaying the splash this will be null

            Dimension ssDim = mySplash.getSize();
            int height = ssDim.height;
            int width  = ssDim.width;

            // stake out some area for our status information

            //splashTextArea     = new Rectangle2D.Double(15., height * 0.88, width * .45, 32.);
            splashTextArea = new Rectangle2D.Double(10, height - 43, width * .65, 20);
            //splashProgressArea = new Rectangle2D.Double(width * .55, height * .92, width * .4, 12);
            splashProgressArea = new Rectangle2D.Double(10, height - 20, width - 20, 10);

            // create the Graphics environment for drawing status info

            splashGraphics = mySplash.createGraphics();
            font = new Font("Dialog", Font.PLAIN, 14);
            splashGraphics.setFont(font);
            
            try{
                fundo = new ImageIcon(getClass().getResource(nomeArquivo));             
            }catch(Exception e){
                e.printStackTrace(System.err);
                fundo = null;
            }
            
            setProgressBar(0);            
        }
    }
    
    /**
     * Display text in status area of Splash.  Note: no validation it will fit.
     * @param str - text to be displayed
     */
    public void drawText(String str, Color corTexto) {
        
        if (mySplash != null && mySplash.isVisible()) {   // important to check here so no other methods need to know if there
            // really is a Splash being displayed
            // erase the last status text
            
            if (fundo == null){
              splashGraphics.setPaint(Color.LIGHT_GRAY);
              splashGraphics.fill(splashTextArea);
            }else{
                 splashGraphics.drawImage(fundo.getImage(), 
                                          (int)splashTextArea.getX(),    (int)splashTextArea.getY(),
                                          (int)splashTextArea.getMaxX(),(int)splashTextArea.getMaxY(),
                                          (int)splashTextArea.getX(),    (int)splashTextArea.getY(),
                                          (int)splashTextArea.getMaxX(),(int)splashTextArea.getMaxY(),
                                          null);
            }
            
            // draw the text
            
            splashGraphics.setPaint(corTexto);
            splashGraphics.drawString(str, (int) (splashTextArea.getX()+10), (int) (splashTextArea.getY()+15));

            // make sure it's displayed
            mySplash.update();
        }        
    }
    
    public void drawText(String str, Color corTexto, long delay) {
        
        drawText(str,corTexto);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            
        }
    }        

    /**
     * Display a (very) basic progress bar
     * @param pct how much of the progress bar to display 0-100
     */
    public final void setProgressBar(int pct) {
        
        if (mySplash != null && mySplash.isVisible()) {

            // Note: 3 colors are used here to demonstrate steps
            // erase the old one
            splashGraphics.setPaint(Color.LIGHT_GRAY);
            splashGraphics.fill(splashProgressArea);

            // draw an outline
            splashGraphics.setPaint(Color.BLUE);
            splashGraphics.draw(splashProgressArea);

            // Calculate the width corresponding to the correct percentage
            int x   = (int) splashProgressArea.getMinX();
            int y   = (int) splashProgressArea.getMinY();
            int wid = (int) splashProgressArea.getWidth();
            int hgt = (int) splashProgressArea.getHeight();

            int doneWidth = Math.round(pct * wid / 100.f);
            doneWidth = Math.max(0, Math.min(doneWidth, wid - 1));  // limit 0-width

            // fill the done part one pixel smaller than the outline
            splashGraphics.setPaint(Color.GREEN);
            splashGraphics.fillRect(x+1, y+1, doneWidth, hgt-1);

            // make sure it's displayed
            mySplash.update();
        }
    }

    public void setProgressBar(int pct, long delay) {
        
        setProgressBar(pct);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            
        }
    }

    public void close() {
        
        if (mySplash != null){
            mySplash.close();
        }
    }
}
