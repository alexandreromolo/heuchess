package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import java.util.Comparator;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 8, 2013
 */
public abstract class EngineRapida extends Engine {

    protected boolean analisarTabuleiroCorBranca;
    
    protected Comparator<Lance> ordenadorLances;
    
    public EngineRapida(Avaliador avaliador, int profundidade){
        
        super(profundidade);
        
        this.avaliador = avaliador;
 
        ordenadorLances = new OrdenaLancesTipoPeca(); 
    }
    
    @Override
    protected double avaliaTabuleiro(final Tabuleiro estadoAvaliado) throws Exception {
        
        double valor = avaliador.avalia(estadoAvaliado, null, analisarTabuleiroCorBranca);            
            
        return valor;
    }
    
    public abstract EngineRapida geraClone();
}
