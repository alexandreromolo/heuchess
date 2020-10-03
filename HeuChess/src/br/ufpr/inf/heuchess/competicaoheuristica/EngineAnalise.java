package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.LanceTreeNode;
import br.ufpr.inf.utils.ArquivoLog;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 8, 2013
 */
public abstract class EngineAnalise extends Engine {
    
    protected int totalMovimentos;    
    protected int totalAvaliacoes;

    protected long   tempoInicial; 
    protected long   tempoGastoNanossegundo;
    
    protected boolean escolherJogadaCorBranca;    
    protected boolean analisarTabuleiroCorBranca;

    protected ArquivoLog arquivoLog;
    
    protected final ArrayList<Lance> lanceTree;
    protected final ArrayList<LanceTreeNode> lanceTreeNodes;
    
    protected double melhorAvaliacao; 
    protected Lance  melhorMovimento;
        
    protected CasoAvaliado  melhorCaso, piorCaso;
        
    protected LanceTreeNode nodeAtual;
    
    protected Comparator<Lance> ordenadorLances;
    
    protected EngineAnalise(final Avaliador avaliador, int profundidade, ArquivoLog arquivoLog) {
        
        super(profundidade);
        
        this.avaliador  = avaliador;
        this.arquivoLog = arquivoLog;
 
        lanceTree      = new ArrayList<>();
        lanceTreeNodes = new ArrayList<>();

        melhorCaso = new CasoAvaliado();
        piorCaso   = new CasoAvaliado();

        ordenadorLances = new OrdenaLancesTipoPeca();        
    }
    
    public long getTempoInicial() {
        return tempoInicial;
    }
    
    public long getTempoGastoNanossegundos() {
        
        assert tempoGastoNanossegundo >= 0;
        
        return tempoGastoNanossegundo;
    }

    public int getTotalMovimentos() {
        
        assert totalMovimentos >= 0;
        
        return totalMovimentos;
    }
    
    public int getTotalAvaliacoes(){
        return totalAvaliacoes;
    }
    
    public CasoAvaliado getMelhorCaso(){
        return melhorCaso;
    }
    
    public CasoAvaliado getPiorCaso(){
        return piorCaso;
    }
    
    public double getValor(){
        return melhorAvaliacao;
    }
    
    public ArrayList<LanceTreeNode> getRaizes() {
        return lanceTreeNodes;
    }    
    
    @Override
    protected double avaliaTabuleiro(final Tabuleiro estadoAvaliado) throws Exception {
        
        double valor = avaliador.avalia(estadoAvaliado, null, analisarTabuleiroCorBranca);            
            
        totalAvaliacoes++;        
        nodeAtual.incrementaAvaliacoes();
            
        if (escolherJogadaCorBranca == analisarTabuleiroCorBranca) {
            
            if (valor > melhorCaso.getValor()) {
                melhorCaso.define(lanceTree, estadoAvaliado, valor);
            } else 
                if (valor == melhorCaso.getValor()) {
                    if (!estadoAvaliado.equals(melhorCaso.getTabuleiro())) {
                        melhorCaso.incrementaQuantidadeIguais();
                    }
                }

            if (valor < piorCaso.getValor()) {
                piorCaso.define(lanceTree, estadoAvaliado, valor);
            } else 
                if (valor == piorCaso.getValor()) {
                    if (!estadoAvaliado.equals(piorCaso.getTabuleiro())) {
                        piorCaso.incrementaQuantidadeIguais();
                    }
                }   
            
        } else {

            if (valor < melhorCaso.getValor()) {
                melhorCaso.define(lanceTree, estadoAvaliado, valor);
            } else 
                if (valor == melhorCaso.getValor()) {
                    if (!estadoAvaliado.equals(melhorCaso.getTabuleiro())) {
                        melhorCaso.incrementaQuantidadeIguais();
                    }
                }

                if (valor > piorCaso.getValor()) {
                    piorCaso.define(lanceTree, estadoAvaliado, valor);
                } else 
                    if (valor == piorCaso.getValor()) {
                        if (!estadoAvaliado.equals(piorCaso.getTabuleiro())) {
                            piorCaso.incrementaQuantidadeIguais();
                        }
                    }
        }
        
        return valor;
    }
    
    public abstract EngineAnalise geraClone();
}
