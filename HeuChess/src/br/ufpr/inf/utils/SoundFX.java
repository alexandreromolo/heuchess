package br.ufpr.inf.utils;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;

public class SoundFX extends Thread{
    
    private boolean   somTocando;
    private AudioClip sound;
    private String    arquivo;    
    
    public SoundFX(String arquivo){        
        setFile(arquivo);
        setPriority(Thread.MAX_PRIORITY);        
    }
        
    public final void setFile(String arquivo){        
        this.arquivo = arquivo;
        carregarSoundFile();                
    }
    
    public String getFile(){
        return arquivo;
    }
    
    public boolean isTocando(){
        return somTocando;
    }
    
    public void close(){        
        if (somTocando){
            stopSound();
        }        
    }
   
    public void play(){
        
        if ((sound != null) && (!somTocando)) {
            somTocando = true;
            sound.play();            
            somTocando = false;
        }else{
            carregarSoundFile();
        }        
    }
    
    public void play(int milisec){        
        
        if ((sound != null) && (!somTocando)) {
            somTocando = true;            
            sound.loop();
            try{
                Thread.sleep(milisec);
            }catch(InterruptedException ie){
                ie.printStackTrace(System.err);
            }
            sound.stop();
            somTocando = false;            
        }else{
            carregarSoundFile();
        }        
    }
     
    public void loop(){        
        if ((sound != null) && (!somTocando)) {
            somTocando = true;
            sound.loop();            
        }else{
            carregarSoundFile();
        }        
    }
     
    public void stopSound(){        
        if ((somTocando) && (sound != null)){
            sound.stop();            
            somTocando = false;
        }else{
            carregarSoundFile();
        }
    }
    
    private void carregarSoundFile(){
        File f = new File(arquivo);
        try {
            sound = Applet.newAudioClip(f.toURL());
            somTocando = false;
        } catch (java.net.MalformedURLException e) {
            sound = null;
            System.out.println("Erro ao carregar o arquivo de Som ["+arquivo+"]");
        }
    }    
    
    @Override
    public void run(){
        play(20000);
    }
}