package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Setembro de 2006, 14:42
 */
public class ExpressaoCalculoHeuristico {
    
    private String nome;
    
    public ExpressaoCalculoHeuristico() {
        nome = "Express�o Padr�o";
    }    
    
    @Override
    public String toString(){
        return nome;
    }
}
