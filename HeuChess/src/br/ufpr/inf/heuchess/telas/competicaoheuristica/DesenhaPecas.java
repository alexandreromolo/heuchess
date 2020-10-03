package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Peca;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import static java.awt.Image.SCALE_SMOOTH;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class DesenhaPecas {

  private final Map<Peca, Cursor> pecaToCursor     = new EnumMap<>(Peca.class);
  private final Map<Peca, Image>  pecaToImage      = new EnumMap<>(Peca.class);
  private final Map<Peca, Image>  pecaToSmallImage = new EnumMap<>(Peca.class);

  private int largura;
  
  public DesenhaPecas(){

  }

  public Cursor getCursor(final Peca peca){
      
    assert peca != null;

    return pecaToCursor.get(peca);
  }

  public Image getImage(final Peca peca){
      
    assert peca != null;

    return pecaToImage.get(peca);
  }

  public Image getSmallImage(final Peca peca){
      
    assert peca != null;

    return pecaToSmallImage.get(peca);
  }

  public int getPecaWidth(){
      return largura;
  }
  
  public void initialize(final int largura, final String textoCaminho){

    assert largura      >= 1;
    assert textoCaminho != null;

    this.largura = largura;
    
    final Toolkit tk       = Toolkit.getDefaultToolkit();
    final int larguraMenor = largura / 2;
    final Dimension d      = tk.getBestCursorSize(largura, largura);
    final Point centro     = new Point(d.width / 2, d.height / 2);
    
    for (final Peca peca : Peca.values()){
        
      final StringBuilder sb = new StringBuilder(textoCaminho);
      
      if (peca.isWhite()){
        sb.append('w');
      }else{
        sb.append('b');
      }
      
      sb.append(peca.getFEN()).append(".png");
      
      final Image imgTmp      = new ImageIcon(getClass().getResource(sb.toString())).getImage();
      final BufferedImage img = new BufferedImage(largura, largura, TYPE_INT_ARGB);
      final Graphics2D g2d    = img.createGraphics();
      
      g2d.drawImage(new ImageIcon(imgTmp.getScaledInstance(largura, largura, SCALE_SMOOTH)).getImage(), 0, 0, null);
      g2d.dispose();
      
      pecaToCursor.put(peca, tk.createCustomCursor(new ImageIcon(imgTmp.getScaledInstance(d.width,d.height, SCALE_SMOOTH)).getImage(), centro, Character.toString(peca.getFEN())));
      pecaToImage.put(peca, img);
      pecaToSmallImage.put(peca, new ImageIcon(imgTmp.getScaledInstance(larguraMenor, larguraMenor, SCALE_SMOOTH)).getImage());
    }
  }
}
