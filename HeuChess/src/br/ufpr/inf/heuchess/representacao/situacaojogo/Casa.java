package br.ufpr.inf.heuchess.representacao.situacaojogo;

import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro.*;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 27 de Junho de 2006, 17:23
 */
public class Casa implements Comparable<Casa> {    
    
    private static final Casa[] CASAS;
    private static final ArrayList<Casa> arrayListCasas;
    
    static {
        CASAS = new Casa[Tabuleiro.TOTAL_CASAS];
        
        int indice = 0;
        
        arrayListCasas = new ArrayList();
        
        for (int linha = 1; linha <= TOTAL_LINHAS; linha++) {
            
            for (int coluna = 1; coluna <= TOTAL_COLUNAS; coluna++) {                
                
                CASAS[indice] = new Casa(coluna, linha);
                arrayListCasas.add(CASAS[indice]);
                indice++;
            }
        }
    }
    
    private int coluna;
    private int indiceColuna;    
    private int linha;
    private int indiceLinha;    
    private int indiceArray;
    
    private String textoFEN;
    
    private DHJOG.Cor cor;    

    private Casa(int coluna, int linha){
                
        if (coluna < 1 || coluna > 8){
            throw new IllegalArgumentException("Valor inválido de coluna [" + coluna + "]");
        }
        
        this.coluna  = coluna;
        indiceColuna = coluna - 1;
                
        if (linha < 1  || linha > 8){
            throw new IllegalArgumentException("Valor inválido de linha [" + linha + "]");
        }
        
        this.linha  = linha;
        indiceLinha = linha - 1;
        
        indiceArray  = indiceColuna + indiceLinha * TOTAL_LINHAS;
        
        textoFEN = nomeColuna(coluna) + linha;
       
        if (linha % 2 == 1) {
            if (coluna % 2 == 1) {
                cor = DHJOG.Cor.PRETAS;
            } else {
                cor = DHJOG.Cor.BRANCAS;
            }
        } else {
            if (coluna % 2 == 1) {
                cor = DHJOG.Cor.BRANCAS;
            } else {
                cor = DHJOG.Cor.PRETAS;
            }
        }
    }

    public String getFEN() {
        return textoFEN;
    }
    
    public int getColuna() {
        return coluna;
    }
   
    public int getIndiceColuna() {
        return indiceColuna;
    }
    
    public int getLinha() {
        return linha;
    }

    public int getIndiceLinha() {
        return indiceLinha;
    }
    
    public int getIndice() {
        return indiceArray;
    }
     
    public DHJOG.Cor getCor() {
        return cor;
    }
        
    public static String nomeColuna(int coluna){       
        return String.valueOf( (char) ('a' + (coluna - 1)) );
    }
    
    @Override
    public String toString(){
        return textoFEN;
    }
    
    @Override
    public int compareTo(final Casa casa) {
        
        if (casa == null) {
            throw new NullPointerException();
        }

        return getFEN().compareTo(casa.getFEN());
    }

    @Override
    public boolean equals(final Object object) {
        
        if (object == this) {
            return true;
        }

        if (!(object instanceof Casa)) {
            return false;
        }

        final Casa o = (Casa) object;
        
        return indiceArray == o.indiceArray;
    }
    
    @Override
    public int hashCode() {
        return indiceArray;
    }

    public static Casa porFEN(final String texto) {
        
        if (texto == null) {
            throw new NullPointerException("Texto com nome da casa é null");
        }
        
        if (texto.length() != 2) {
            throw new IllegalArgumentException("Texto com o nome da casa inválido [" + texto + ']');
        }

        return porIndices(texto.charAt(0) - 'a', texto.charAt(1) - '1');
    }

    public static Casa porIndice(final int indice) {
        
        assert (indice >= 0) && (indice < TOTAL_CASAS);

        return CASAS[indice];
    }
    
    public static Casa porIndices(final int indiceColuna, final int indiceLinha) {
        
        if ((indiceColuna < 0) || (indiceColuna >= TOTAL_COLUNAS)) {
            throw new IllegalArgumentException("Valor inválido de Coluna [" + indiceColuna + ']');
        }
        
        if ((indiceLinha < 0)  || (indiceLinha  >= TOTAL_LINHAS)) {
            throw new IllegalArgumentException("Valor inválido de linha [" + indiceLinha + ']');
        }

        return CASAS[indiceColuna + indiceLinha * TOTAL_COLUNAS];
    }

    public static ArrayList<Casa> todoTabuleiro(){
        return  arrayListCasas;
    }
}