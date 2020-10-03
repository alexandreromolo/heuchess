package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Sep 20, 2012
 */
public class CasoAvaliado {

    private Tabuleiro tabuleiroAvaliado;
    private double    valor;
    private int       quantidadeIguais;
    
    private final ArrayList<Lance> lances;        
    
    public CasoAvaliado() {
        lances = new ArrayList<>();    
    }

    public double getValor() {
        return valor;
    }

    public Tabuleiro getTabuleiro(){        
        return tabuleiroAvaliado;
    }
    
    public ArrayList<Lance> getLances(){
        return lances;
    }
    
    public int getQuantidadeIguais() {
        return quantidadeIguais;
    }
    
    public void incrementaQuantidadeIguais(){
        quantidadeIguais++;
    }
    
    public void reinicia(double valorInicial){
        
        lances.clear();
        tabuleiroAvaliado = null;        
        quantidadeIguais  = 0;
        
        valor = valorInicial;
    }
    
    public void define(ArrayList<Lance> novosLances, Tabuleiro novoTabuleiro, double novoValor){
        
        lances.clear();        
        quantidadeIguais = 0;
        
        lances.addAll(novosLances);        
        
        valor = novoValor;
        tabuleiroAvaliado = novoTabuleiro;        
    }    
}