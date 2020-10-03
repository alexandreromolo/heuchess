/*
 * FuncaoTransferable.java
 *
 * Created on 20 de Julho de 2006, 16:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class FuncaoTransferable implements Transferable {

  public static DataFlavor FUNCAO_FLAVOR = new DataFlavor(Funcao.class,"Funcao");

  DataFlavor flavors[] = { FUNCAO_FLAVOR };

  Funcao funcao;

  public FuncaoTransferable(Funcao tp) {
    funcao = tp;
  }

  public synchronized DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor.getRepresentationClass() == Funcao.class);
  }

  public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
        return (Object) funcao;
    } else {
        throw new UnsupportedFlavorException(flavor);
    }
  }
} 