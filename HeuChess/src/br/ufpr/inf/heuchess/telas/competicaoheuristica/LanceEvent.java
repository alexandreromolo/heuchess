package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import java.util.EventObject;

public class LanceEvent extends EventObject {

    private static final long serialVersionUID = -558192316749148649L;
    
    private final Lance lance;

    public LanceEvent(final Object object, final Lance lance) {

        super(object);

        assert lance != null;

        this.lance = lance;
    }

    public Lance getLance() {

        assert lance != null;

        return lance;
    }
}
