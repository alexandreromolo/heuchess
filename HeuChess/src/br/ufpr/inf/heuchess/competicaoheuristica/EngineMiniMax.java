package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.LanceTreeNode;
import br.ufpr.inf.utils.ArquivoLog;
import java.util.Arrays;

public class EngineMiniMax extends EngineAnalise {
    
    public EngineMiniMax(final Avaliador avaliador, int profundidade, ArquivoLog arquivoLog) {
        super(avaliador, profundidade, arquivoLog);
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
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("findMin - Profundidade " + (profundidadeBusca - profundidade));                
            }
            
            return avaliaTabuleiro(tabuleiro);
        }
        
        double resposta = Double.POSITIVE_INFINITY;
        
        totalMovimentos += movimentos.length;
        nodeAtual.adicionaFilhos(movimentos.length);
        
        Arrays.sort(movimentos, ordenadorLances);
        
        for (final Lance movimento : movimentos) {
            
            if (!buscaAtivada){
                return 0;
            }
            
            final Tabuleiro novoEstado = tabuleiro.derive(movimento, true);
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("findMin - Profundidade " + (profundidadeBusca - profundidade) + 
                                            " Movimento " + movimento + " Novo Estado [" + tabuleiro.getFEN() + "]");
            }
            
            lanceTree.add(movimento);
        
            final double avaliacao = findMax(novoEstado, profundidade - 1);
            
            lanceTree.remove(movimento);
            
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
           
            if (arquivoLog != null){
                arquivoLog.registraMensagem("findMax - Profundidade " + (profundidadeBusca - profundidade));
            }
            
            return avaliaTabuleiro(tabuleiro);            
        }
        
        double resposta = Double.NEGATIVE_INFINITY;
        
        totalMovimentos += movimentos.length;
        nodeAtual.adicionaFilhos(movimentos.length);
        
        Arrays.sort(movimentos, ordenadorLances);
        
        for (final Lance movimento : movimentos) {
        
            if (!buscaAtivada){
                return 0;
            }
            
            final Tabuleiro novoEstado = tabuleiro.derive(movimento, true);
            
            if (arquivoLog != null){
                arquivoLog.registraMensagem("findMax - Profundidade " + (profundidadeBusca - profundidade) + 
                                            " Movimento " + movimento + " Novo Estado [" +  tabuleiro.getFEN() + "]");
            }
            
            lanceTree.add(movimento);
            
            final double avaliacao = findMin(novoEstado, profundidade - 1);
            
            lanceTree.remove(movimento);
            
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
            
            tempoInicial = System.nanoTime();

            if (arquivoLog != null) {
                arquivoLog.registraLinhaSeparacao();
                arquivoLog.registraMensagem("Iniciando Análise Minimax para peças " + (estadoAtual.isWhiteActive() ? "Brancas" : "Pretas"));
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
            
            totalAvaliacoes = 0;
            totalMovimentos = movimentos.length;

            melhorAvaliacao = 0;
            melhorMovimento = movimentos[0];       
            
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
                
                Tabuleiro novoEstado = estadoAtual.derive(movimento, true);
                
                LanceTreeNode node = new LanceTreeNode(estadoAtual, movimento, novoEstado, folha);
                
                lanceTreeNodes.add(node);
                nodeAtual = node;
                
                if (escolherJogadaParaMim){
                    
                    double valorHeuristico = findMin(novoEstado, profundidadeBusca - 1);
                
                    nodeAtual.setValor(valorHeuristico);
                    
                    if (valorHeuristico > melhorValorHeuristico) {

                        melhorValorHeuristico = valorHeuristico;
                        melhorMovimento       = movimento;
                    }
                    
                }else{
                    double valorHeuristico = findMax(novoEstado, profundidadeBusca - 1);
                
                    nodeAtual.setValor(valorHeuristico);
                    
                    if (valorHeuristico < melhorValorHeuristico) {

                        melhorValorHeuristico = valorHeuristico;
                        melhorMovimento       = movimento;
                    }
                }
                
                lanceTree.remove(movimento);
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
    
    @Override
    public String getDescricao() {
        return "Busca Completa";
    }
    
    @Override
    public EngineMiniMax geraClone(){
        return new EngineMiniMax(avaliador, profundidadeBusca, arquivoLog);
    }
}