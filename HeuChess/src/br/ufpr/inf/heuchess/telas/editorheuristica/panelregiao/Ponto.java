/*
 * Ponto.java
 *
 * Created on 3 de Agosto de 2006, 14:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class Ponto implements Elemento {

  private DesenhaRegioes tabuleiro;
  
  private Color  color;
  private Casa   casa;
  
  public Ponto(DesenhaRegioes tabuleiro, Casa casa, Color color) {    
    this.tabuleiro = tabuleiro;
    this.casa      = casa;    
    this.color     = color;
  }
  
  public void setTabuleiro(DesenhaRegioes tabuleiro){
    this.tabuleiro = tabuleiro;
  }
  
  public DesenhaRegioes getTabuleiro(){
      return tabuleiro;
  }

  public Rectangle getBounds() {      
    return tabuleiro.posicaoCasa(casa);
  }

  public Elemento getTranslated(int dX, int dY) {
    
    Rectangle posicaoAtual = getBounds();    
    posicaoAtual.x += dX;
    posicaoAtual.y += dY;
    
    Dimension dist1 = tabuleiro.distanciaEsquerdaTopo(posicaoAtual.x, posicaoAtual.y);
    Dimension dist2 = tabuleiro.distanciaDireitaBase(posicaoAtual.x + posicaoAtual.width, posicaoAtual.y + posicaoAtual.height);
    
    int novoX = 0;
    int novoY = 0;
    
    if (dist1.width < dist2.width){
        novoX = posicaoAtual.x;
    }else{
        novoX = posicaoAtual.x + posicaoAtual.width;
    }
    
    if (dist1.height < dist2.height){
        novoY = posicaoAtual.y;
    }else{
        novoY = posicaoAtual.y + posicaoAtual.height;
    }        
        
    return new Ponto(tabuleiro,tabuleiro.casaPosXY(novoX, novoY),color);
  }

  public void paint(Graphics2D g) {
    
    g.setColor(color);    
    float alpha = .8f;
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));        
        
    Rectangle posicaoAtual = getBounds();
    g.fillRect(posicaoAtual.x, posicaoAtual.y, posicaoAtual.width, posicaoAtual.height);    
    
    // Desenha Borda //
    
    g.setColor(DesenhaRegioes.corBorda);        
    g.setStroke(new BasicStroke(2.0F)); 
    g.drawRect(posicaoAtual.x,posicaoAtual.y,posicaoAtual.width,posicaoAtual.height);
  }
  
  public Casa[] getCasas(){
      Casa[] casas = {casa};
      return casas;
  }
}
    