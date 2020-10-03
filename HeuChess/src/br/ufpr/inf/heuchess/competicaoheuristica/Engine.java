package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 8, 2013
 */
public abstract class Engine {
    
    public static final int PROFUNDIDADE_MINIMA_BUSCA = 1;    
    public static final int PROFUNDIDADE_MAXIMA_BUSCA = 7;
    
    protected int profundidadeBusca;
    
    protected Avaliador avaliador;    
    
    protected boolean buscaAtivada;
        
    protected Engine(int profundidade) {
        setProfundidadeBusca(profundidade);
    }
    
    public int getProfundidadeMaximaBusca() {
        
        assert PROFUNDIDADE_MAXIMA_BUSCA >= profundidadeBusca;
        
        return PROFUNDIDADE_MAXIMA_BUSCA;
    }

    public int getProfundidadeMinimaBusca() {
        
        assert PROFUNDIDADE_MINIMA_BUSCA <= profundidadeBusca;
        
        return PROFUNDIDADE_MINIMA_BUSCA;
    }

    public int getProfundidadeBusca() {
        
        assert profundidadeBusca >= PROFUNDIDADE_MINIMA_BUSCA;
        
        return profundidadeBusca;
    }
    
    public final void setProfundidadeBusca(int limite) {
        
        if (limite < PROFUNDIDADE_MINIMA_BUSCA || limite > PROFUNDIDADE_MAXIMA_BUSCA){            
            throw new IllegalArgumentException("Valor da Profundidade de Busca Inválido [" + limite + "]");
        }
        
        profundidadeBusca = limite;
    }
 
    public void setAvaliador(Avaliador avaliador){
        this.avaliador = avaliador;
    }
    
    public Avaliador getAvaliador(){
        return avaliador;
    }
    
    public void cancelaBusca(){
        
        buscaAtivada = false;
        
        avaliador.cancelaAvaliacao();
    }
    
    public abstract Lance getProximoLance(final Tabuleiro tabuleiro, final boolean analisarTabuleiroCorBranca) throws Exception;  
    
    public abstract String getDescricao();
 
    protected abstract double avaliaTabuleiro(final Tabuleiro estadoAvaliado) throws Exception;
}