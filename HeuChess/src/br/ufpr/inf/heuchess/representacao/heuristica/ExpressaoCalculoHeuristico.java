package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Setembro de 2006, 14:42
 */
public class ExpressaoCalculoHeuristico {
    
    private String nome;
    
    public ExpressaoCalculoHeuristico() {
        nome = "Expressão Padrão";
    }    
    
    @Override
    public String toString(){
        return nome;
    }
}
