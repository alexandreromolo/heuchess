package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import java.util.Random;
import javax.swing.JTextPane;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 8, 2013
 */
public class EngineRandomica extends Engine {
        
    private static final Random RANDOMIZER = new Random();
      
    /* Criado apenas para compatibilidade com os pré-requisitos da Classe Engine */
    
    public class AvaliadorVazio implements Avaliador {
        
        @Override
        public double avalia(final Tabuleiro tabuleiro, final JTextPane jTextPane, final boolean corJogadorBranco) throws Exception {
            return 0;
        }
    
        @Override
        public void cancelaAvaliacao(){
            
        }
    }
    
    public EngineRandomica(){
        
        super(1);
        
        setAvaliador(new AvaliadorVazio());
    }
    
    @Override
    public Lance getProximoLance(Tabuleiro tabuleiro, boolean analisarTabuleiroCorBranca) throws Exception {
        
        assert tabuleiro != null;

        //////////////////////////////////////////////////////////////////////
        // Realiza a procura pelo melhor movimento a partir do estado atual //
        //////////////////////////////////////////////////////////////////////

        final Lance[] movimentos = tabuleiro.getMovimentosValidosJogador(tabuleiro.isWhiteActive());
            
        if (movimentos.length <= 0) {
            throw new IllegalArgumentException("Não existem jogadas válidas para o jogador com as " + (tabuleiro.isWhiteActive() ? "Brancas" : "Pretas"));
        }
        
        Lance lance = movimentos[RANDOMIZER.nextInt(movimentos.length)];
        
        return lance;
    }

    @Override
    public String getDescricao() {
        return "Escolha Aleatória";
    }

    @Override
    protected double avaliaTabuleiro(Tabuleiro estadoAvaliado) throws Exception {
        return 0;
    }
}