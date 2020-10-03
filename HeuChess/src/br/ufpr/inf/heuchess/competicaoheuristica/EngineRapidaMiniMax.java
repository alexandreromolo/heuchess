package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import java.util.Arrays;

public class EngineRapidaMiniMax extends EngineRapida {
    
    public EngineRapidaMiniMax(final Avaliador avaliador, int profundidade) {
        super(avaliador, profundidade);
    }
    
    /**
     * Min - Simula a escolha do Adversário
     */
    private double findMin(final Tabuleiro tabuleiro, final int profundidade) throws Exception {
        
        assert tabuleiro    != null;
        assert profundidade >= 0;
        assert tabuleiro.isWhiteActive() != analisarTabuleiroCorBranca;
        
        final Lance[] movimentos = tabuleiro.getMovimentosValidosJogador(tabuleiro.isWhiteActive());
        
        if (profundidade == 0 || tabuleiro.podeEmpatar50Movimentos() || tabuleiro.podeEmpatarTriplaRepeticao() || movimentos.length == 0) {
            return avaliaTabuleiro(tabuleiro);
        }
        
        double resposta = Double.POSITIVE_INFINITY;
        
        Arrays.sort(movimentos, ordenadorLances);
        
        for (final Lance movimento : movimentos) {
            
            if (!buscaAtivada){
                return 0;
            }
            
            final Tabuleiro novoEstado = tabuleiro.derive(movimento, true);
            
            final double avaliacao = findMax(novoEstado, profundidade - 1);
            
            if (avaliacao < resposta) {
                resposta = avaliacao;
            }
        }

        return resposta;
    }
   
    /**
     * Max - Simula a escolha do Jogador
     */
    private double findMax(final Tabuleiro tabuleiro, final int profundidade) throws Exception {
        
        assert tabuleiro    != null;
        assert profundidade >= 0;
        assert tabuleiro.isWhiteActive() == analisarTabuleiroCorBranca;

        final Lance[] movimentos = tabuleiro.getMovimentosValidosJogador(tabuleiro.isWhiteActive());
        
        if (profundidade == 0 || tabuleiro.podeEmpatar50Movimentos() || tabuleiro.podeEmpatarTriplaRepeticao() || movimentos.length == 0) {
            return avaliaTabuleiro(tabuleiro);            
        }
        
        double resposta = Double.NEGATIVE_INFINITY;
        
        Arrays.sort(movimentos, ordenadorLances);
        
        for (final Lance movimento : movimentos) {
        
            if (!buscaAtivada){
                return 0;
            }
            
            final Tabuleiro novoEstado = tabuleiro.derive(movimento, true);
            
            final double avaliacao = findMin(novoEstado, profundidade - 1);
            
            if (avaliacao > resposta) {
                resposta = avaliacao;
            }
        }

        return resposta;
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
                
                Tabuleiro novoEstado = estadoAtual.derive(movimento, true);
                
                if (escolherJogadaParaMim){
                    
                    double valorHeuristico = findMin(novoEstado, profundidadeBusca - 1);
                
                    if (valorHeuristico > melhorValorHeuristico) {

                        melhorValorHeuristico = valorHeuristico;
                        melhorMovimento       = movimento;
                    }
                    
                }else{
                    double valorHeuristico = findMax(novoEstado, profundidadeBusca - 1);
                
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
        return "Busca Completa";
    }
    
    @Override
    public EngineRapidaMiniMax geraClone(){
        return new EngineRapidaMiniMax(avaliador, profundidadeBusca);
    }
}