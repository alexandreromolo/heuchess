/*
 * fileFilter.java
 *
 * Created on 10 de Julho de 2007, 17:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.utils;
import java.io.*;
/**
 *
 * @author Luis Bueno
 */
          
 public class FiltroArquivo extends javax.swing.filechooser.FileFilter {
   
    private String filter; 
    private String description; 
        
    public FiltroArquivo( String filter, String description ){ 
       setFilter( filter ); 
       setDescription( description );       
    } 
     
    public void setFilter( String filter ){ 
       if( filter != null ) 
          this.filter = filter.toLowerCase(); 
    } 
     
    public String getFilter(){ 
       return filter; 
    } 
     
    public String getDescription(){ 
       return description; 
    } 
     
    public void setDescription( String description ){ 
       this.description = description; 
    } 
     
    public boolean accept( File f ){ 
       if( f != null ){ 
          if(f.isDirectory()) return true; 
           
          String extension = getExtension( f ); 
          if(extension != null && extension.equals( filter ))  return true; 
       } 
       return false; 
    } 
     
    public String getExtension( File f ){ 
       if( f != null ){ 
          String fileName = f.getName(); 
          int i = fileName.lastIndexOf( '.' ); 
          if( i > 0 && i < fileName.length() - 1 ){ 
             return fileName.substring( i + 1 ).toLowerCase(); 
          } 
       }       
       return null; 
    } 
}
