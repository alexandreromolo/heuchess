package br.ufpr.inf.utils.gui;

import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 5, 2012
 */
public class ElementoLista {
    
    final private long  id;
    final private Class classe;
    
    private String nome;
    private Tipo   tipo;
    private String descricao;
    
    public ElementoLista(long id, String nome, String descricao, Class classe, Tipo tipo){
        this.id        = id;
        this.nome      = nome;
        this.descricao = descricao;
        this.classe    = classe;
        this.tipo      = tipo;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Class getClasse() {
        return classe;
    }

    public Tipo getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nome;
    }
}