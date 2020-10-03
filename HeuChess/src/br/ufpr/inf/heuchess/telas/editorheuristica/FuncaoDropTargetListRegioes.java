package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Regiao;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import javax.swing.JList;

/**
 * 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 20 de Julho de 2006, 16:51
 */
public class FuncaoDropTargetListRegioes implements DropTargetListener {
  
    private DropTarget     dropTarget;    
    private PanelEtapa     panelEtapa;
    private JList          jList;
  
    public FuncaoDropTargetListRegioes(PanelEtapa panelEtapa, JList jList){    
        this.panelEtapa = panelEtapa;
        this.jList      = jList;        
        dropTarget      = new DropTarget(jList, this);        
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
        
        if (jList.isEnabled()){
            
            try{
                Transferable tr      = dtde.getTransferable();
                DataFlavor[] flavors = tr.getTransferDataFlavors();
                
                for (int i = 0; i < flavors.length; i++) {
                    
                    if (tr.isDataFlavorSupported(flavors[i])) {
                        
                        dtde.acceptDrop(dtde.getDropAction());
                        Object objeto = tr.getTransferData(flavors[i]);
                        
                        if (objeto instanceof Funcao){
                            
                            int indice = jList.locationToIndex(dtde.getLocation());                            
                            if (indice != -1){
                                
                                Rectangle rect = jList.getCellBounds(indice,indice);                                
                                
                                if ((rect.x <= dtde.getLocation().x) && (dtde.getLocation().x <= (rect.x + rect.width)) &&
                                    (rect.y <= dtde.getLocation().y) && (dtde.getLocation().y <= (rect.y + rect.height))){
                                    
                                    Regiao regiao = (Regiao) jList.getModel().getElementAt(indice);
                                    
                                    if (regiao != null){
                                        panelEtapa.funcaoArrastada((Funcao)objeto,regiao);
                                        dtde.dropComplete(true);
                                        return;
                                    }
                                }
                            }
                            
                            UtilsGUI.dialogoErro(null,"Não existe nenhuma região na posição onde a Função foi solta!");    
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
}