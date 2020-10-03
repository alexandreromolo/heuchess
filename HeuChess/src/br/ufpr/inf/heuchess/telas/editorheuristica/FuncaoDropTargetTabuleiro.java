package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Regiao;
import br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao.DesenhaRegioes;
import br.ufpr.inf.utils.gui.UtilsGUI;
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
public class FuncaoDropTargetTabuleiro implements DropTargetListener {
  
    private DropTarget     dropTarget;    
    private PanelEtapa     panelEtapa;
    private DesenhaRegioes panelTabuleiro;
  
    public FuncaoDropTargetTabuleiro(PanelEtapa panelEtapa, DesenhaRegioes tabuleiro){    
        this.panelEtapa     = panelEtapa;
        this.panelTabuleiro = tabuleiro;        
        dropTarget          = new DropTarget(tabuleiro, this);        
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
                        
                        Regiao regiao = panelTabuleiro.localizaRegiaoAlvo(dtde.getLocation().x,dtde.getLocation().y);
                        
                        if (regiao != null){
                            panelEtapa.funcaoArrastada((Funcao)objeto,regiao);
                            dtde.dropComplete(true);
                            return;
                        }else{
                            UtilsGUI.dialogoErro(null,"Não existe nenhuma região na posição onde a Função foi solta!");        
                        }
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