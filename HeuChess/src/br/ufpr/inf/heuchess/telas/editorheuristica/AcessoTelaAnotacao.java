package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 18 de Julho de 2006, 16:34
 */
public interface AcessoTelaAnotacao extends ModalFrameHierarchy {
  
    public abstract void fechandoTelaAnotacao(boolean sucesso); 
    
    public abstract JList   getJListAnotacoes();    
    public abstract JLabel  getJLabelTotalAnotacoes();        
    public abstract JButton getJButtonAbrirAnotacao();
    public abstract JButton getJButtonExcluirAnotacao();
                    
    public abstract Componente getComponente();
    
    public abstract boolean podeAlterar();
    
    public abstract void atualizaVersaoDataUltimaModificacao();
}
