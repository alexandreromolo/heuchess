package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import java.util.Arrays;

public class TabelaTransposicao {
    
    private static final int QUANTIDADE_BUSCAS = 3;
        
    public static enum MotivoAvaliacao {
        CORTE_ALPHA,             // Tabuleiro calculado por corte Alpha
        CORTE_BETA,              // Tabuleiro calculado por corte Beta        
        FIM_PROFUNDIDADE,        // Tabuleiro calculado por fim da profundidade de Busca
        NENHUM_MOVIMENTO_VALIDO, // Tabuleiro não permite nenhum movimento válido para o Jogador
        EMPATE_50_MOVIMENTOS,    // Tabuleiro calculado por Empate pela Regra de 50 Movimentos
        EMPATE_TRIPLA_REPETICAO; // Tabuleiro calculado por Empate pela Regra de Tripla Repetição
    }
    
    private final int[]    hashCodes;    
    private final byte[]   profundidades;   
    private final double[] valores;
    private final MotivoAvaliacao[] motivosAvaliacao;   
    
    public TabelaTransposicao(final int capacidade) {
        
        assert capacidade > 0;

        hashCodes        = new int[capacidade];
        profundidades    = new byte[capacidade];
        valores          = new double[capacidade];
        motivosAvaliacao = new MotivoAvaliacao[capacidade];                
    }
    
    public void clear() {
        Arrays.fill(hashCodes, 0);
    }
    
    public Double get(final Tabuleiro tabuleiro, final int profundidade, final double alpha, final double beta) {
        
        assert tabuleiro != null;

        final int hashCodeProcurado = tabuleiro.hashCode();
        final int capacidade        = hashCodes.length;
        
        int pos = Math.abs(hashCodeProcurado % capacidade);
        int hashCodeAtual = hashCodes[pos];
        
        for (int i = QUANTIDADE_BUSCAS; hashCodeAtual != 0 && --i >= 0; ) {
            
            if (hashCodeAtual == hashCodeProcurado) {
                
                if (profundidades[pos] >= profundidade) {
                    
                    final MotivoAvaliacao tipo = motivosAvaliacao[pos];                    
                    final double valor = valores[pos];
                    
                    if ((tipo == MotivoAvaliacao.FIM_PROFUNDIDADE)              || 
                        (tipo == MotivoAvaliacao.NENHUM_MOVIMENTO_VALIDO)       ||    
                        (tipo == MotivoAvaliacao.CORTE_ALPHA && valor <= alpha) ||                             
                        (tipo == MotivoAvaliacao.CORTE_BETA  && valor >= beta)  ||                            
                        (tipo == MotivoAvaliacao.EMPATE_50_MOVIMENTOS    && tabuleiro.podeEmpatar50Movimentos()) ||    
                        (tipo == MotivoAvaliacao.EMPATE_TRIPLA_REPETICAO && tabuleiro.podeEmpatarTriplaRepeticao())) {
                                            
                        return Double.valueOf(valor);
                    }
                }
                
                break;
            }
            
            pos++;
            
            if (pos >= capacidade) {
                pos -= capacidade;
            }
            
            hashCodeAtual = hashCodes[pos];
        }

        return null;
    }

    public void put(final Tabuleiro tabuleiro, final int profundidade, final double valor, MotivoAvaliacao motivo) {
        
        assert tabuleiro != null;

        final int hashCodeTabuleiro = tabuleiro.hashCode();
        final int capacidade = hashCodes.length;
        
        int pos = Math.abs(hashCodeTabuleiro % capacidade);
        int hashCodeAtual = hashCodes[pos];
        
        for (int i = QUANTIDADE_BUSCAS; hashCodeAtual != 0 && hashCodeAtual != hashCodeTabuleiro && --i >= 0; ) {
            
            pos++;
            
            if (pos >= capacidade) {
                pos -= capacidade;
            }
            
            hashCodeAtual = hashCodes[pos];
        }

        if (hashCodeAtual == 0 || (hashCodeAtual == hashCodeTabuleiro && profundidades[pos] < profundidade)) {
            
            hashCodes[pos]        = hashCodeTabuleiro;            
            profundidades[pos]    = (byte) profundidade;            
            valores[pos]          = valor;
            motivosAvaliacao[pos] = motivo;
        }
    }
}