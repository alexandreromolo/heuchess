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
 * Created on 31 de Julho de 2006, 16:43
 */
public class Retangulo implements Elemento {

    private DesenhaRegioes tabuleiro;
    
    private Color color;
    private Casa  casaInicial;
    private Casa  casaFinal;

    public Retangulo(DesenhaRegioes tabuleiro, Casa casaInicial, Casa casaFinal, Color color) {
        this.tabuleiro   = tabuleiro;
        this.casaInicial = casaInicial;
        this.casaFinal   = casaFinal;
        this.color       = color;
    }

    @Override
    public void setTabuleiro(DesenhaRegioes tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    @Override
    public DesenhaRegioes getTabuleiro() {
        return tabuleiro;
    }

    @Override
    public Rectangle getBounds() {

        Rectangle posicaoInicial = tabuleiro.posicaoCasa(casaInicial);
        Rectangle posicaoFinal   = tabuleiro.posicaoCasa(casaFinal);

        return new Rectangle(posicaoInicial.x, 
                             posicaoInicial.y,
                            (posicaoFinal.x + posicaoFinal.width)  - posicaoInicial.x,
                            (posicaoFinal.y + posicaoFinal.height) - posicaoInicial.y);
    }

    @Override
    public Elemento getTranslated(int dX, int dY) {

        Rectangle posicaoAtual = getBounds();

        posicaoAtual.x += dX;
        posicaoAtual.y += dY;

        Dimension dist1 = tabuleiro.distanciaEsquerdaTopo(posicaoAtual.x, posicaoAtual.y);
        Dimension dist2 = tabuleiro.distanciaDireitaBase(posicaoAtual.x + posicaoAtual.width, posicaoAtual.y + posicaoAtual.height);

        int novoXInicial;
        int novoYInicial;

        if (dist1.width < dist2.width) {
            novoXInicial = posicaoAtual.x;
        } else {
            novoXInicial = posicaoAtual.x + dist2.width + (tabuleiro.comprimentoCasa / 2);
        }

        if (dist1.height < dist2.height) {
            novoYInicial = posicaoAtual.y;
        } else {
            novoYInicial = posicaoAtual.y + dist2.height + (tabuleiro.comprimentoCasa / 2);
        }

        Casa casa = tabuleiro.casaPosXY(novoXInicial, novoYInicial);
        Rectangle posicaoNovaCasaInicial = tabuleiro.posicaoCasa(casa);

        return new Retangulo(tabuleiro,
                             casa,
                             tabuleiro.casaPosXY(posicaoNovaCasaInicial.x + posicaoAtual.width, posicaoNovaCasaInicial.y + posicaoAtual.height),
                             color);
    }

    @Override
    public void paint(Graphics2D g) {

        g.setColor(color);
        float alpha = .8f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        Rectangle posicaoAtual = getBounds();

        g.fillRect(posicaoAtual.x, posicaoAtual.y, posicaoAtual.width, posicaoAtual.height);

        // Desenha Borda //

        g.setColor(DesenhaRegioes.corBorda);
        g.setStroke(new BasicStroke(2.0F));
        g.drawRect(posicaoAtual.x, posicaoAtual.y, posicaoAtual.width, posicaoAtual.height);
    }

    @Override
    public Casa[] getCasas() {

        int colunaInicial = casaInicial.getColuna();
        int colunaFinal   = casaFinal.getColuna();
        int linhaInicial  = casaInicial.getLinha();
        int linhaFinal    = casaFinal.getLinha();

        Casa[] casas = new Casa[(linhaInicial - linhaFinal + 1) * (colunaFinal - colunaInicial + 1)];

        int indice = 0;
        
        for (int lin = linhaInicial; lin >= linhaFinal; lin--) {
            for (int col = colunaInicial; col <= colunaFinal; col++) {
                casas[indice] = Casa.porIndices(col-1, lin-1);
                indice++;
            }
        }

        return casas;
    }
}