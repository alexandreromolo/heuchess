package br.ufpr.inf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemNotFoundException;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class PropertiesManager {
 
    private Properties PROPERTIES = new Properties();
    
    private String     arquivoProperties;
  
    public PropertiesManager(String arquivo) {

        arquivoProperties = arquivo + ".properties";
        
        InputStream in = PropertiesManager.class.getClassLoader().getResourceAsStream(arquivoProperties);
        
        if (in != null) {            
            try {
                PROPERTIES.load(in);
            } catch (IOException e) {
                throw new MissingResourceException("Erro no carregamento do arquivo Properties [" + arquivoProperties + "]",null,null);
            }
        }else {
            throw new FileSystemNotFoundException("Arquivo [" + arquivoProperties + "] n�o localizado");
        }
    }

    public String getString(final String chave){
        
        if (chave == null || chave.trim().length() == 0){
            throw new IllegalArgumentException("N�o passado valor de chave");  
        }
       
        String res = PROPERTIES.getProperty(chave);
        
        if (res == null){
            throw new IllegalArgumentException("N�o foi localizada a chave [" + chave + "] no arquivo [" + arquivoProperties + "]");            
        }
        return res;
    }
}