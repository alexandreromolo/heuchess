package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Aug 3, 2012
 */
public interface AcessoTelaUsuario extends ModalFrameHierarchy {
    
    public abstract void fechandoTelaUsuario(Usuario usuario, boolean novo); 

}
