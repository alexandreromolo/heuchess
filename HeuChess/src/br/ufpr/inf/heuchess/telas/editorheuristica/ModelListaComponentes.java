package br.ufpr.inf.heuchess.telas.editorheuristica;

import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 17 de Julho de 2006, 10:03
 */
public class ModelListaComponentes extends DefaultListModel {
    
    private ArrayList objetos;
    
    public ModelListaComponentes(ArrayList listaObjetos) {
        
        super();
        
        this.objetos = listaObjetos;        
        
        for (int x = 0; x < listaObjetos.size(); x++){
            super.addElement(listaObjetos.get(x));            
        }
    }        
    
    @Override
    public void addElement(Object objeto){
        
        objetos.add(objeto);
        
        super.addElement(objeto);                        
    }    
    
    @Override
    public void add(int posicao, Object objeto){
        
        objetos.add(posicao,objeto);
        
        super.add(posicao, objeto); 
    }
    
    @Override
    public boolean removeElement(Object objeto){
        
        objetos.remove(objeto);
        
        return super.removeElement(objeto);        
    }
    
    @Override
    public Object remove(int indice){
        
        objetos.remove(indice);
        
        return super.remove(indice);
    }
    
    @Override
    public Object set(int indice, Object objeto){        
        
        objetos.set(indice, objeto);        
        
        return super.set(indice, objeto);
    }
    
    public boolean moveUmParaInicio(int posicao){
        
        if (posicao <= 0){
            return false;
        }
        if (posicao > objetos.size()-1){
            return false;
        }
        
        Object objeto = objetos.get(posicao);
        
        objetos.remove(posicao);
        
        super.remove(posicao);
        
        posicao--;
        
        objetos.add(posicao,objeto);
        
        super.add(posicao, objeto);         
        
        return true;
    }
    
    public boolean moveUmParaFim(int posicao){
        
        if (posicao < 0){
            return false;
        }       
        
        if (posicao >= objetos.size()-1){
            return false;
        }
        
        Object objeto = objetos.get(posicao);
        
        objetos.remove(posicao);
        
        super.remove(posicao);
        
        posicao++;
        
        objetos.add(posicao,objeto);
        
        super.add(posicao, objeto);
        
        return true;
    }   
}
