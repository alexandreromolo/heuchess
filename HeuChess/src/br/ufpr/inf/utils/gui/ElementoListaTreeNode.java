package br.ufpr.inf.utils.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Aug 24, 2012
 */
public class ElementoListaTreeNode extends DefaultMutableTreeNode {

    private boolean checado;
    private boolean expandido;

    public ElementoListaTreeNode(ElementoLista elemento) {
        super(elemento);
        
        checado   = true;
        expandido = false;
    }

    public ElementoLista getElementoLista() {
        return (ElementoLista) super.getUserObject();
    }

    public boolean isChecado() {
        return checado;
    }

    public void setChecado(boolean checado) {
        this.checado = checado;        
    }

    public boolean isExpandido() {
        return expandido;
    }

    public void setExpandido(boolean expandido) {
        this.expandido = expandido;
    }
}
