package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import static br.ufpr.inf.heuchess.competicaoheuristica.TabelaTransposicao.MotivoAvaliacao.*;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.LanceTreeNode;
import br.ufpr.inf.utils.ArquivoLog;
import java.util.Arrays;

public class EngineAlphaBetaNega extends EngineAnalise {

    private int totalCortesAlpha;
    private int totalCortesBeta;
    private int totalCortesTransposicao;
            
    private final TabelaTransposicao TRANSPOSICAO = new TabelaTransposicao(1000000);
    
    public EngineAlphaBetaNega(final Avaliador avaliador, int profundidade, ArquivoLog arquivoLog) {
        super(avaliador, profundidade, arquivoLog);        
    }    

    private double alphaBetaNega(final Tabuleiro tabuleiro, final int profundidade, double alpha, double beta, boolean escolherJogadaParaMim) throws Exception {
        
        assert tabuleiro    != null;
        assert profundidade >= 0;
        assert (escolherJogadaParaMim ? tabuleiro.isWhiteActive() == analisarTabuleiroCorBranca : tabuleiro.isWhiteActive() != analisarTabuleiroCorBranca);
        
        final Double resultadoPrevio = TRANSPOSICAO.get(tabuleiro, profundidade, alpha, beta);
        
        if (resultadoPrevio != null) {
            totalCortesTransposicao++;
            return resultadoPrevio.doubleValue();
        }
        
        if (profundidade == 0) {
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("alphaBetaNega - Profundidade Final " + (profundidadeBusca - profundidade));                
            }
            
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, FIM_PROFUNDIDADE);
            return valor;
        }
        
        if (tabuleiro.podeEmpatar50Movimentos()) {
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("alphaBetaNega - Empate por 50 Movimentos " + (profundidadeBusca - profundidade));                
            }
            
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, EMPATE_50_MOVIMENTOS);
            return valor;
        }
        
        if (tabuleiro.podeEmpatarTriplaRepeticao()) {
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("alphaBetaNega - Empate por Tripla Repetição " + (profundidadeBusca - profundidade));                
            }
            
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, EMPATE_TRIPLA_REPETICAO);
            return valor;
        }
        
        final Lance[] movimentos = tabuleiro.getMovimentosValidosJogador(tabuleiro.isWhiteActive());
        
        if (movimentos.length == 0) {
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("alphaBetaNega - Nenhum Movimento válido " + (profundidadeBusca - profundidade));                
            }
            
            double valor = avaliaTabuleiro(tabuleiro);
            TRANSPOSICAO.put(tabuleiro, profundidade, valor, NENHUM_MOVIMENTO_VALIDO);
            return valor;
        }
               
        totalMovimentos += movimentos.length;
        nodeAtual.adicionaFilhos(movimentos.length);
                
        Arrays.sort(movimentos, ordenadorLances);
        
        for (final Lance movimento : movimentos) {
            
            if (!buscaAtivada){
                return 0;
            }
            
            final Tabuleiro tabuleiroDerivado = tabuleiro.derive(movimento, true);
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("alphaBetaNega - Profundidade " + (profundidadeBusca - profundidade) + 
                                            " Movimento " + movimento + " Novo Estado [" + tabuleiro.getFEN() + "]");
            }
            
            lanceTree.add(movimento);
                        
            final double avaliacao = alphaBetaNega(tabuleiroDerivado, profundidade - 1, alpha, beta, !escolherJogadaParaMim);
            
            lanceTree.remove(movimento);
            
            if (escolherJogadaParaMim) {
                
                if (avaliacao >= beta) {
                    totalCortesBeta++;
                    TRANSPOSICAO.put(tabuleiro, profundidade, beta, CORTE_BETA);
                    return beta;
                }
                
                if (avaliacao > alpha) {
                    alpha = avaliacao; // Alfa é o valor mais alto do jogador - Max (EU)
                }
                
            } else {
                
                if (avaliacao <= alpha) {
                    totalCortesAlpha++;
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
            
            tempoInicial = System.nanoTime();

            if (arquivoLog != null) {
                arquivoLog.registraLinhaSeparacao();
                arquivoLog.registraMensagem("Iniciando Análise Minimax AlphaBetaNega para peças " + (estadoAtual.isWhiteActive() ? "Brancas" : "Pretas"));
                arquivoLog.registraMensagem("Tabuleiro Atual [" + estadoAtual.getFEN() + "]");
            }

            //////////////////////////////////////////////////////////////////////
            // Realiza a procura pelo melhor movimento a partir do estado atual //
            //////////////////////////////////////////////////////////////////////

            final Lance[] movimentos = estadoAtual.getMovimentosValidosJogador(estadoAtual.isWhiteActive());
            
            if (movimentos.length <= 0) {
                throw new IllegalArgumentException("Não existem jogadas válidas para o jogador com as " + (estadoAtual.isWhiteActive() ? "Brancas" : "Pretas"));
            }

            if (arquivoLog != null) {
                arquivoLog.registraMensagem("Total de lances iniciais possíveis: " + movimentos.length);
            }
            
            buscaAtivada = true;
            escolherJogadaCorBranca = estadoAtual.isWhiteActive();
            this.analisarTabuleiroCorBranca = analisarTabuleiroCorBranca;
            
            totalCortesAlpha        = 0;
            totalCortesBeta         = 0;
            totalCortesTransposicao = 0;
            
            totalAvaliacoes = 0;
            totalMovimentos = movimentos.length;

            melhorAvaliacao = 0;
            melhorMovimento = movimentos[0];       
            
            TRANSPOSICAO.clear();
            
            lanceTree.clear();
            lanceTreeNodes.clear();
            nodeAtual = null;
            
            double melhorValorHeuristico;
            
            boolean escolherJogadaParaMim = (escolherJogadaCorBranca == analisarTabuleiroCorBranca);
                    
            if (escolherJogadaParaMim){                
                
                melhorCaso.reinicia(Double.NEGATIVE_INFINITY);
                piorCaso.reinicia(Double.POSITIVE_INFINITY);    
                
                melhorValorHeuristico = Double.NEGATIVE_INFINITY;
            }else{                                
                melhorCaso.reinicia(Double.POSITIVE_INFINITY);
                piorCaso.reinicia(Double.NEGATIVE_INFINITY);
                
                melhorValorHeuristico = Double.POSITIVE_INFINITY;
            }

            boolean folha = (profundidadeBusca == 1 ? true : false);
            
            Arrays.sort(movimentos, ordenadorLances);
            
            for (final Lance movimento : movimentos) {

                if (!buscaAtivada) {
                    break;
                }
                
                lanceTree.add(movimento);
                
                Tabuleiro tabuleiroDerivado = estadoAtual.derive(movimento, true);
                
                LanceTreeNode node = new LanceTreeNode(estadoAtual, movimento, tabuleiroDerivado, folha);
                
                lanceTreeNodes.add(node);
                nodeAtual = node;
                
                final double valorHeuristico = alphaBetaNega(tabuleiroDerivado, profundidadeBusca - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !escolherJogadaParaMim);
                
                nodeAtual.setValor(valorHeuristico);                
                lanceTree.remove(movimento);
                                
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

            melhorAvaliacao = melhorValorHeuristico;
            
            ///////////////
            // Resultado //
            ///////////////
            
            if (buscaAtivada) {

                tempoGastoNanossegundo = System.nanoTime() - tempoInicial;

                if (arquivoLog != null) {
                    arquivoLog.registraLinhaSeparacao();
                    arquivoLog.registraMensagem("Total de Nós Gerados                   : " + totalMovimentos);
                    arquivoLog.registraMensagem("Total de Nós Avaliados Heurísticamente : " + totalAvaliacoes);
                    arquivoLog.registraMensagem("Total de Cortes Alpha realizados       : " + totalCortesAlpha);
                    arquivoLog.registraMensagem("Total de Cortes Beta  realizados       : " + totalCortesBeta);
                    arquivoLog.registraMensagem("Total de Cortes Tabela Transposição    : " + totalCortesTransposicao);
                    arquivoLog.registraMensagem("Melhor valor Heurístico                : " + melhorAvaliacao);
                    arquivoLog.registraMensagem("Tempo Gasto                            : " + tempoGastoNanossegundo + " ns");
                    arquivoLog.registraMensagem("Movimento Escolhido                    : " + melhorMovimento.toString());
                }

                assert melhorMovimento != null;
                
                return melhorMovimento;

            } else {
                if (arquivoLog != null) {
                    arquivoLog.registraMensagem("Busca cancelada");
                }
                
                return null;
            }

        } catch (Exception e) {
            
            HeuChess.registraExcecao(e);            
            if (arquivoLog != null) {
                arquivoLog.registraExcecao(e);
            }
            throw e;
        }
    }
    
    public int getTotalCortesAlpha(){
        return totalCortesAlpha;
    }
    
    public int getTotalCortesBeta(){
        return totalCortesBeta;
    }
    
    public int getTotalCortesTransposicao(){
        return totalCortesTransposicao;
    }
    
    @Override
    public String getDescricao() {
        return "Busca Otimizada";
    }
    
    @Override
    public EngineAlphaBetaNega geraClone(){        
        return new EngineAlphaBetaNega(avaliador, profundidadeBusca, arquivoLog);
    }
}
