package br.ufpr.inf.utils;

import java.awt.Toolkit;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 17 de Junho de 2006, 16:20
 */
public final class Utils {
    
    public static boolean abrePaginaWeb(String url) {

        if (!java.awt.Desktop.isDesktopSupported()) {
            System.err.println("Desktop is not supported (fatal)");
            return false;            
        }

        if (url == null || url.trim().length() == 0) {
            System.out.println("Usage: OpenURI [URI [URI ... ]]");
            return false;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            System.err.println("Desktop doesn't support the browse action (fatal)");
            return false;
        }

       try {
            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace(System.err);
            return false;
        }

        return true;
    }

    
    
    public static String geraMD5(String texto) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(texto.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            String retornaSenha = hash.toString(16);
            return retornaSenha;
        } catch (Exception ns) {
            ns.printStackTrace(System.err);
        }

        return null;
    }
    /*
    public static void registraException(Exception exception){
        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, exception);
    }
    */
    public static boolean pertence(Object obj, ArrayList lista) {
        
        if (lista.indexOf(obj) != -1){
            return true;
        }else{
            return false;
        }
    }
    
    public static boolean verificaEstadoTecla(int tecla) {
        
        // KeyEvent.VK_CAPS_LOCK
        
        Toolkit tk = Toolkit.getDefaultToolkit();

        if (tk.getLockingKeyState(tecla)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     public static void drawStringAlinhada(Graphics g, String texto, int x, int y, int alinhamento){
        
        FontMetrics fm   = g.getFontMetrics(g.getFont());
        java.awt.geom.Rectangle2D rect = fm.getStringBounds(texto, g);
        
        int textHeight = (int)(rect.getHeight());
        int textWidth  = (int)(rect.getWidth());
        
        if ((alinhamento & ALIN_VERTICAL_TOPO) == ALIN_VERTICAL_TOPO){
            // NÃO FAZ NADA //
        }else
            if ((alinhamento & ALIN_VERTICAL_CENTRALIZADO) == ALIN_VERTICAL_CENTRALIZADO){
                y -= (textHeight/2) + fm.getAscent();    
            }else
                if ((alinhamento & ALIN_VERTICAL_BASE) == ALIN_VERTICAL_BASE){
                    y -= textHeight + fm.getAscent();    
                }
    
        if ((alinhamento & ALIN_HORIZONTAL_ESQUERDA) == ALIN_HORIZONTAL_ESQUERDA){
            // NÃO FAZ NADA //
        }else
            if ((alinhamento & ALIN_HORIZONTAL_CENTRALIZADO) == ALIN_HORIZONTAL_CENTRALIZADO){
                x -= (textWidth/2);    
            }else
                if ((alinhamento & ALIN_HORIZONTAL_DIREITA) == ALIN_HORIZONTAL_DIREITA){
                    x -= textWidth;    
                }
        
        g.drawString(texto, x, y);        
    }
    */
}