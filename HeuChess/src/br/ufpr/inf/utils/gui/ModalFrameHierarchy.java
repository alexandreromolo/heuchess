package br.ufpr.inf.utils.gui;

import java.awt.Frame;

/**
 * Interface que define a obrigatoriedade de definir a rela��o entre frames que ser�o
 * mostrados como modal
 * 
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public interface ModalFrameHierarchy {
    
    public abstract Frame getFrame();
    
    public abstract ModalFrameHierarchy getModalOwner();
    
}
