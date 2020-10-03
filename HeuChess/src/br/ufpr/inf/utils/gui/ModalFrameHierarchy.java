package br.ufpr.inf.utils.gui;

import java.awt.Frame;

/**
 * Interface que define a obrigatoriedade de definir a relação entre frames que serão
 * mostrados como modal
 * 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public interface ModalFrameHierarchy {
    
    public abstract Frame getFrame();
    
    public abstract ModalFrameHierarchy getModalOwner();
    
}
