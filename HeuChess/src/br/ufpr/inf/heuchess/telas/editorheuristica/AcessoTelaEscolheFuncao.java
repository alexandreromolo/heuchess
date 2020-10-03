package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 4 de Setembro de 2006, 11:07
 */
public interface AcessoTelaEscolheFuncao extends ModalFrameHierarchy {
    
    public void fechandoTelaEscolheFuncao(Funcao funcaoEscolhida);
   
}
