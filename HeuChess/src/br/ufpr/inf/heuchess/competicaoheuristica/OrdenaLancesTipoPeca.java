package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Peca;
import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;
import java.util.Comparator;

public class OrdenaLancesTipoPeca implements Comparator<Lance> {

    @Override
    public int compare(final Lance lance1, final Lance lance2) {
        
        final Peca presa1 = lance1.getPecaCapturada();
        final Peca presa2 = lance2.getPecaCapturada();
        
        if (presa1 == null && presa2 != null) {            
            return 1;
        } else 
            if (presa1 != null && presa2 == null) {
                return -1;                
            }else
                if (presa1 != null && presa2 != null){                                    
                    if (valorTipo(presa1.getTipo()) > valorTipo(presa2.getTipo())) {
                        return -1;
                    }else
                        if (valorTipo(presa1.getTipo()) < valorTipo(presa2.getTipo())) {
                            return 1;                        
                        }
                }

        final int valor1 = valorTipo(lance1.getPeca().getTipo());
        final int valor2 = valorTipo(lance2.getPeca().getTipo());
        
        if (valor1 > valor2) {
            return -1;
        } else 
            if (valor1 < valor2) {
                return 1;
            }
        
        return 0;
    }
    
    public static int valorTipo(TipoPeca tipo){
        
        switch(tipo){
            
            case PEAO:  
                return 100;
                
            case TORRE: 
                return 550;
                
            case CAVALO:
                return 300;
                
            case BISPO: 
                return 350;
                
            case DAMA:  
                return 1000;
                
            default: // Caso do Rei //  
                return 0; 
        }
    }
}
