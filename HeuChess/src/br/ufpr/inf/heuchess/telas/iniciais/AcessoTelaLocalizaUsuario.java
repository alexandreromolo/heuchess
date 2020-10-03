package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 31, 2012
 */
public interface AcessoTelaLocalizaUsuario extends ModalFrameHierarchy {

    public abstract void fechandoTelaLocalizaUsuario(Usuario usuario); 
}
