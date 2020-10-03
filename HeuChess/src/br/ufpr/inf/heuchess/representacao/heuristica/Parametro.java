package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 19 de Julho de 2006, 17:19
 */
public class Parametro {
    
    private final String         nome;
    private final String         descricao; 
    private final DHJOG.TipoDado tipo;
             
    public Parametro(String nome, DHJOG.TipoDado tipo, String descricao) {        
        this.nome      = nome;
        this.tipo      = tipo;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public DHJOG.TipoDado getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }
}
