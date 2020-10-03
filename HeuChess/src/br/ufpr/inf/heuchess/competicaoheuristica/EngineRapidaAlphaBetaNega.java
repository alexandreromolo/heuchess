package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import static br.ufpr.inf.heuchess.competicaoheuristica.TabelaTransposicao.MotivoAvaliacao.*;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import java.util.Arrays;

public class EngineRapidaAlphaBetaNega extends EngineRapida {
                                                        
    private final TabelaTransposicao TRANSPOSICAO = new TabelaTransposicao(1000000);
    
    public EngineRapidaAlphaBetaNega(final Avaliador avaliador, int profundidade) {
        super(avaliador, profundidade);        
    }    

    private double alphaBetaNega(final Tabuleiro tabuleiro, final int profundidade, double alpha, double beta, boolean escolherJogadaParaMim) throws Exception {
        
        assert tabuleiro    != null;
        assert profundidade >= 0;
        assert (escolherJogadaParaMim ? tabuleiro.isWhiteActive() == analisarTabuleiroCorBranca : tabuleiro.isWhiteActive() != analisarTabuleiroCorBranca);
        
        final Double resultadoPrevio = TRANSPOSICAO.get(tabuleiro, profundidade, alpha, beta);
        
        if (resultadoPrevio != null) {
            return resultadoPrevio.doubleValue();
        }
        
        if (profundidade == 0) {
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, FIM_PROFUNDIDADE);
            return valor;
        }
        
        if (tabuleiro.podeEmpatar50Movimentos()) {
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, EMPATE_50_MOVIMENTOS);
            return valor;
        }
        
        if (tabuleiro.podeEmpatarTriplaRepeticao()) {
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, EMPATE_TRIPLA_REPETICAO);
            return valor;
        }
        
        final Lance[] movimentos = tabuleiro.getMovimentosValidosJogador(tabuleiro.isWhiteActive());
        
        if (movimentos.length == 0) {
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, NENHUM_MOVIMENTO_VALIDO);
            return valor;
        }
               
        Arrays.sort(movimentos, ordenadorLances);
        
        for (final Lance movimento : movimentos) {
            
            if (!buscaAtivada){
                return 0;
            }
            
            final Tabuleiro tabuleiroDerivado = tabuleiro.derive(movimento, true);
            
            final double avaliacao = alphaBetaNega(tabuleiroDerivado, profundidade - 1, alpha, beta, !escolherJogadaParaMim);
            
            if (escolherJogadaParaMim) {
                
                if (avaliacao >= beta) {
                    TRANSPOSICAO.put(tabuleiro, profundidade, beta, CORTE_BETA);
                    return beta;
                }
                
                if (avaliacao > alpha) {
                    alpha = avaliacao; // Alfa é o valor mais alto do jogador - Max (EU)
                }
                
            } else {
                
                if (avaliacao <= alpha) {
                    TRANSPOSICAO.put(tabuleiro, profundidade, alpha, CORTE_ALPHA);
                    return alpha;
                }

                if (avaliacao < beta) {
                    beta = avaliacao; // Beta é o valor mais baixo do jogador - Min (ADVERSÁRIO)
                }
            }
        }

        if (escolherJogadaParaMim){
            TRANSPOSICAO.put(tabuleiro, profundidade, alpha, CORTE_ALPHA);
            return alpha;
        }else{
            TRANSPOSICAO.put(tabuleiro, profundidade, beta, CORTE_BETA);        
            return beta;
        }
    }
        
    @Override
    public synchronized Lance getProximoLance(final Tabuleiro estadoAtual, final boolean analisarTabuleiroCorBranca) throws Exception {
        
        assert estadoAtual != null;

        try {
            //////////////////////////////////////////////////////////////////////
            // Realiza a procura pelo melhor movimento a partir do estado atual //
            //////////////////////////////////////////////////////////////////////

            final Lance[] movimentos = estadoAtual.getMovimentosValidosJogador(estadoAtual.isWhiteActive());
            
            if (movimentos.length <= 0) {
                throw new IllegalArgumentException("Não existem jogadas válidas para o jogador com as " + (estadoAtual.isWhiteActive() ? "Brancas" : "Pretas"));
            }

            buscaAtivada = true;
            this.analisarTabuleiroCorBranca = analisarTabuleiroCorBranca;
            
            Lance melhorMovimento = movimentos[0];       
            
            TRANSPOSICAO.clear();
            
            double melhorValorHeuristico;
            
            boolean escolherJogadaParaMim = (estadoAtual.isWhiteActive() == analisarTabuleiroCorBranca);
                    
            if (escolherJogadaParaMim){                
                melhorValorHeuristico = Double.NEGATIVE_INFINITY;
            }else{                                
                melhorValorHeuristico = Double.POSITIVE_INFINITY;
            }

            Arrays.sort(movimentos, ordenadorLances);
            
            for (final Lance movimento : movimentos) {

                if (!buscaAtivada) {
                    break;
                }
                
                Tabuleiro tabuleiroDerivado = estadoAtual.derive(movimento, true);
                
                final double valorHeuristico = alphaBetaNega(tabuleiroDerivado, profundidadeBusca - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !escolherJogadaParaMim);
                
                if (escolherJogadaParaMim){
                    
                    if (valorHeuristico > melhorValorHeuristico) {                
                        
                        melhorValorHeuristico = valorHeuristico;
                        melhorMovimento       = movimento;
                    }                    
                }else{                                        
                    if (valorHeuristico < melhorValorHeuristico) {

                        melhorValorHeuristico = valorHeuristico;
                        melhorMovimento       = movimento;
                    }
                }
            }

            ///////////////
            // Resultado //
            ///////////////
            
            if (buscaAtivada) {

                assert melhorMovimento != null;
                
                return melhorMovimento;

            } else {
                
                return null;
            }

        } catch (Exception e) {
            HeuChess.registraExcecao(e);            
            throw e;
        }
    }
    
    @Override
    public String getDescricao() {
        return "Busca Otimizada";
    }
    
    @Override
    public EngineRapidaAlphaBetaNega geraClone(){        
        return new EngineRapidaAlphaBetaNega(avaliador, profundidadeBusca);
    }
}