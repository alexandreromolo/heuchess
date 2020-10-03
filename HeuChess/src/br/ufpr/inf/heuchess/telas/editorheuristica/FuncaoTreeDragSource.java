package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import java.awt.dnd.*;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 20 de Julho de 2006, 15:51
 */
public class FuncaoTreeDragSource implements DragSourceListener, DragGestureListener {

    private DragSource             source;
    private DragGestureRecognizer  recognizer;
    private FuncaoTransferable     transferable;
    private DefaultMutableTreeNode oldNode;
    private JTree                  sourceTree;

    public FuncaoTreeDragSource(JTree tree, int actions) {
        sourceTree = tree;
        source     = new DragSource();
        recognizer = source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
    }

    /**
     * Drag Gesture Handler
     */
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {

        oldNode = (DefaultMutableTreeNode) sourceTree.getLastSelectedPathComponent();

        if (oldNode == null || !(oldNode.getUserObject() instanceof Funcao)) {
            return;
        }

        transferable = new FuncaoTransferable((Funcao) oldNode.getUserObject());
        source.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
        
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
        
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        
        /**
         * if (!dsde.getDropSuccess()){ Utils.dialogoErro(null,"Não é possível
         * arrastar uma Função para este componente!\n" + "Arraste uma Funcao
         * para uma Tipo de Peça ou para uma Região do Tabuleiro.");
         *
         * }
         */
    }
}