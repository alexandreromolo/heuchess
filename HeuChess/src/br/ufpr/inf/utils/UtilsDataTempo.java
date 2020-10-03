package br.ufpr.inf.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Sep 17, 2012
 */
public class UtilsDataTempo {

    private final static SimpleDateFormat formataTempo;
    private final static SimpleDateFormat formataData;
    private final static SimpleDateFormat formataDataTempo;

    static {
        formataTempo = new SimpleDateFormat("H:mm:ss");
        formataTempo.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        formataData = new SimpleDateFormat("dd/MM/yyyy");
        
        formataDataTempo = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formataDataTempo.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String getDataHora() {

        Date data = new Date();
        
        return formataDataTempo.format(data);
    }

    public static String formataData(Date data) {

        if (data == null) {
            return null;
        }

        return formataData.format(data);
    }

    public static String formataTempo(final Date date) {

        assert date != null;

        return formataTempo.format(date);
    }
    
    public static String formataTempoMilissegundos(long tempo) {

        assert tempo >= 0;

        Date date = new Date(tempo);

        return formataTempo(date);
    }
    
    public static String formataTempoMilissegundos(long tempo, boolean sigla) {

        if (tempo < 0) {
            throw new IllegalArgumentException("Valor de tempo negativo!");
        }

        if (tempo < 1000) {

            // milisegundos //

            return String.valueOf(tempo) + (sigla ? " ms" : " milisegundos");
        }
        
        return formataTempoMilissegundos(tempo);        
    }
    
    public static String formataTempoNanossegundos(long tempo, boolean sigla) {

        if (tempo < 0){
            throw new IllegalArgumentException("Valor de tempo negativo!");
        }
        
        StringBuilder sb = new StringBuilder();

        if (tempo < 1000) {
            
            // nanossegundos //
            
            sb.append(tempo);
            sb.append(sigla ? " ns": " nanosegundos");
            
        } else 
            if (tempo < 1000000) {                
                
                // microssegundos //
                
                double valor = tempo / 1000F;
                
                sb.append(UtilsString.formataDouble("##0.00",valor));
                sb.append(sigla ? " \u00B5s" : " microsegundos");
                
            } else 
                if (tempo < 1000000000) {
                    
                    // milissegundos //
                    
                    double valor = tempo / 1000000F;
                    
                    sb.append(UtilsString.formataDouble("##0.00",valor));
                    sb.append(sigla ? " ms" : " milisegundos");
                    
                } else{
                    
                    // segundos //
                                         
                    long valor = tempo / 1000000;
                                        
                    sb.append(formataTempoMilissegundos(valor));
                }
        
        return sb.toString();
    }
    
    public static Date converteToDate(String texto, String formato) throws Exception {
        
        SimpleDateFormat sdf = new SimpleDateFormat(formato);             

        return sdf.parse(texto);  
    }
}
