package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import javax.swing.JTextPane;

public interface Avaliador {
    
    public double avalia(final Tabuleiro tabuleiro, final JTextPane jTextPane, final boolean corJogadorBranco) throws Exception;
    
    public void cancelaAvaliacao();
}
