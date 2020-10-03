package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import java.util.EventListener;

public interface LanceListener extends EventListener {

    public void moved(final LanceEvent event);
}
