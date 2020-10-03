package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Sep 14, 2012
 */
public class LanceTreeNode extends DefaultMutableTreeNode {

    private final Tabuleiro tabuleiro;
    private final Tabuleiro tabuleiroDerivado;
    private final Lance     lance;
    private final String    textoSAN;
    private boolean         folha;    
    
    private double valor;
    private int    totalFilhos;
    private int    totalAvaliacoes;
    
    private Partida.Estado tipoEmpate;
       
    public LanceTreeNode(Tabuleiro tabuleiro, Lance lance, Tabuleiro tabuleiroDerivado, boolean folha) {
        super(lance);
        
        this.tabuleiro = tabuleiro;
        this.lance     = lance;
        this.folha     = false;        
        this.textoSAN  = lance.toSAN(tabuleiro, tabuleiroDerivado);
        
        this.tabuleiroDerivado = tabuleiroDerivado;
        
        final Lance[] movimentos = tabuleiroDerivado.getMovimentosValidosJogador(tabuleiroDerivado.isWhiteActive());

        ///////////////////////////////////////////////////////////////
        // Verifica se é estado terminal de partida (Xeque ou Empate //
        ///////////////////////////////////////////////////////////////
        if (tabuleiroDerivado.podeEmpatar50Movimentos()){
            tipoEmpate = Partida.Estado.DRAWN_BY_50_MOVE_RULE;
            this.folha = true;
        }else
            if (tabuleiroDerivado.podeEmpatarTriplaRepeticao()){
                tipoEmpate = Partida.Estado.DRAWN_BY_TRIPLE_REPETITION;
                this.folha = true;
            }else
                if (movimentos.length == 0){
                    tipoEmpate = Partida.Estado.STALEMATE;
                    this.folha = true;
                }else{
                    this.folha = folha;
                }
    }
    
    @Override
    public boolean isLeaf(){
        return folha;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Tabuleiro getTabuleiroDerivado(){
        return tabuleiroDerivado;
    }
    
    public Lance getLance() {
        return lance;
    }

    public String getTextoSAN(){
        return textoSAN;
    }
    
    public double getValor() {
        return valor;
    }

    public void setValor(double valorRaiz) {
        this.valor = valorRaiz;
    }

    public int getTotalFilhos() {
        return totalFilhos;
    }

    public void adicionaFilhos(int quantidade) {
        totalFilhos += quantidade;
    }

    public int getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    public void incrementaAvaliacoes() {
        totalAvaliacoes++;
    }
    
    public Partida.Estado tipoEmpate(){
        return tipoEmpate;
    }
}