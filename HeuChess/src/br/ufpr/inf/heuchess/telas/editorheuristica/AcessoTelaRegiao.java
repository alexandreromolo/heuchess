package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Regiao;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 3 de Setembro de 2006, 11:23
 */
public interface AcessoTelaRegiao extends ModalFrameHierarchy {
 
    public abstract void fechandoTelaRegiao(Regiao regiao);    

}
