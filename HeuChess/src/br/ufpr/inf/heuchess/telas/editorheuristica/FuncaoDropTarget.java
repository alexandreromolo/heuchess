package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 20 de Julho de 2006, 16:51
 */
public class FuncaoDropTarget implements DropTargetListener {
  
    private DropTarget dropTarget;
    private Object     tipoDestino;
    private PanelEtapa panelEtapa;
  
    public FuncaoDropTarget(PanelEtapa panelEtapa, Component comp, Object tipo){    
        this.panelEtapa = panelEtapa;
        tipoDestino     = tipo; 
        dropTarget      = new DropTarget(comp,this);
    }
  
    @Override
    public void dragEnter(DropTargetDragEvent dtde){
        dtde.acceptDrag(dtde.getDropAction());        
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde){
        dtde.acceptDrag(dtde.getDropAction());        
    }
    
    @Override
    public void dragExit(DropTargetEvent dte){
        
    }
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde){
        
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde){
        
        try{
            Transferable tr      = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            
            for (int i = 0; i < flavors.length; i++) {
                
                if (tr.isDataFlavorSupported(flavors[i])) {
                    
                    dtde.acceptDrop(dtde.getDropAction());
                    Object objeto = tr.getTransferData(flavors[i]);
                    
                    if (objeto instanceof Funcao){
                        
                        Funcao funcao = (Funcao) objeto;
                        
                        panelEtapa.funcaoArrastada(funcao,tipoDestino);
                        
                        dtde.dropComplete(true);
                        return;
                    }
                    
                    dtde.dropComplete(false);                    
                    return;
                }
            }            
            
        }catch(UnsupportedFlavorException | IOException e){
            HeuChess.registraExcecao(e);
        }
        
        dtde.rejectDrop();
    }
}