/*
 * AcessoTelaPreencheFuncao.java
 *
 * Created on 3 de Setembro de 2006, 12:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.FuncaoPreenchida;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public interface AcessoTelaPreencheFuncao extends ModalFrameHierarchy {
    
    public void fechandoTelaPreencheFuncao(FuncaoPreenchida funcaoPreenchida, Object elemento);
   
}
