package br.ufpr.inf.heuchess;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Nov 30, 2012
 */
public enum Propriedade {

    SITUACAO_ACESSO_SISTEMA(54);
    
    private final long id;

    private Propriedade(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
