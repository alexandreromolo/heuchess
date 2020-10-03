package br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface Elemento {
    
  public void setTabuleiro(DesenhaRegioes tabuleiro);
  
  public DesenhaRegioes getTabuleiro();
  
  public Rectangle getBounds();
  
  public void paint(Graphics2D g);
  
  public Elemento getTranslated(int dX, int dY);
  
  public Casa[] getCasas();
  
}
  
