package br.ufpr.inf.heuchess.representacao.situacaojogo;

import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 27 de Junho de 2006, 17:23
 */
public enum TipoPeca {
    
    PEAO("Pe�o",    DHJOG.PALAVRAS_RESERVADAS[0], '\000'),
    TORRE("Torre",  DHJOG.PALAVRAS_RESERVADAS[1], 'T'), // Em Ingl�s R
    CAVALO("Cavalo",DHJOG.PALAVRAS_RESERVADAS[2], 'C'), // Em Ingl�s N  
    BISPO("Bispo",  DHJOG.PALAVRAS_RESERVADAS[3], 'B'), // Em Ingl�s B
    DAMA("Dama",    DHJOG.PALAVRAS_RESERVADAS[4], 'D'), // Em Ingl�s Q
    REI("Rei",      DHJOG.PALAVRAS_RESERVADAS[5], 'R'); // Em Ingl�s K   
    
    private final String nome;
    private final String textoDHJOG;
    private final char   letraSAN; 
        
    private TipoPeca(String nome, String textoDHJOG, char letraSAN) {
        
        if (nome == null || nome.trim().length() == 0){
            throw new IllegalArgumentException("Valor inv�lido de Nome de Tipo de Pe�a [" + nome + "]");
        }
        
        if (textoDHJOG == null || textoDHJOG.trim().length() == 0){
            throw new IllegalArgumentException("Valor inv�lido de Texto DHJOG para Tipo de Pe�a [" + nome + "]");
        }
        
        this.nome       = nome.trim();        
        this.textoDHJOG = textoDHJOG.trim();
        this.letraSAN   = letraSAN;
    }
    
    public String getNome(){
        return nome;
    }
    
    public String toDHJOG(){
        return textoDHJOG;
    }
    
    public char getLetraSAN() {
        return letraSAN;
    }
    
    @Override
    public String toString(){
        return nome;
    }
}