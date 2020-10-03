package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Nov 10, 2012
 */
public interface AcessoTelaParametrosPartida extends ModalFrameHierarchy {
    
    public abstract void fechandoTelaParametrosPartida(final Partida game);

}
