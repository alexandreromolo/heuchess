package br.ufpr.inf.utils.gui;

import br.ufpr.inf.utils.gui.ModalFrameUtil.EventPump;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import javax.swing.JFrame;
 
/**
 * @author Santhosh Kumar T - santhosh@in.fiorano.com 
 * Classe foi alterada incluindo novos eventos e modificações
 */ 
public class ModalFrameUtil{
    
    static class EventPump implements InvocationHandler{ 
        
        Frame frame; 
 
        public EventPump(Frame frame){ 
            this.frame = frame; 
        } 
 
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { 
            return frame.isShowing() ? Boolean.TRUE : Boolean.FALSE; 
        } 
 
        // when the reflection calls in this method has to be 
        // replaced once Sun provides a public API to pump events. 
        
        public void start() throws Exception { 
            
            Class clazz = Class.forName("java.awt.Conditional"); 
            
            Object conditional = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this); 
            
            Method pumpMethod = Class.forName("java.awt.EventDispatchThread").getDeclaredMethod("pumpEvents", new Class[]{clazz}); 
            
            pumpMethod.setAccessible(true); 
            pumpMethod.invoke(Thread.currentThread(), new Object[]{conditional}); 
        } 
    } 
 
    public static void showAsModalBlock(final ModalFrameHierarchy frameChild){ 
        processa(frameChild,true,true,true,false);
    }
    
    public static void showAsModalDontBlock(final ModalFrameHierarchy frameChild){ 
        processa(frameChild,true,true,false,false);
    }
    
    public static void showAsModalOwnerInvisibleDontBlock(final ModalFrameHierarchy frameChild){ 
        processa(frameChild,true,true,false,true);
    }
        
    private static void processa(final ModalFrameHierarchy frameChild,
                                 final boolean invisivelAoMinimizar,
                                 final boolean ativarChild, 
                                 final boolean bloquear,
                                 final boolean ocultarOwner){ 
                
        if (frameChild == null){
            throw new InvalidParameterException("frameChild é null");
        }
        
        final ModalFrameHierarchy frameOwner = frameChild.getModalOwner();
                
        //final boolean ownerVisivelInicialmente = (frameOwner != null ? frameOwner.getFrame().isVisible() : false);
                
        frameChild.getFrame().addWindowListener(new WindowAdapter(){ 
            
            @Override
            public void windowOpened(WindowEvent e){
                
                //System.err.println("Entrou no windowOpened [" + frameChild.getFrame().getTitle() + "] : " + Utils.getDataHora());
                
                if (frameOwner != null) {
                    if (frameOwner.getFrame().isEnabled()){
                        //System.err.println("ENABLED FALSE [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                        frameOwner.getFrame().setEnabled(false);                                    
                    }                    
                    if (ocultarOwner){
                        //System.err.println("VISIBLE FALSE [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                        frameOwner.getFrame().setVisible(false);
                    }
                }
            } 
                    
            @Override
            public void windowClosed(WindowEvent e){                 
                
                //System.err.println("Entrou no windowClosed [" + frameChild.getFrame().getTitle() + "] : " + Utils.getDataHora());
                
                if (frameOwner != null) {
                    
                    ArrayList<ModalFrameHierarchy> framesList = new ArrayList();
                    
                    ModalFrameHierarchy fram = frameOwner;
                    while (fram.getModalOwner() != null) {
                        fram = fram.getModalOwner();
                        framesList.add(fram);
                    }

                    for (int x = framesList.size() - 1; x >= 0; x--) {

                        if (!framesList.get(x).getFrame().isVisible()) {
                            //System.err.println("VISIBLE TRUE " + framesList.get(x).getFrame().getTitle() + "] : " + Utils.getDataHora());
                            framesList.get(x).getFrame().setVisible(true);
                            
                        }
                        //System.err.println("JFRAME.NORMAL [" + framesList.get(x).getFrame().getTitle() + "] : " + Utils.getDataHora());
                        if (framesList.get(x).getFrame().getExtendedState() == JFrame.ICONIFIED){//x
                            framesList.get(x).getFrame().setExtendedState(JFrame.NORMAL);
                        }//x    
                        framesList.get(x).getFrame().toFront();
                    }

                    //if (!ownerVisivelInicialmente){
                    
                        if (!frameOwner.getFrame().isVisible()) {
                            //System.err.println("VISIBLE TRUE [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                            frameOwner.getFrame().setVisible(true);
                        }
                        if (!frameOwner.getFrame().isEnabled()) {
                            //System.err.println("ENABLED TRUE [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                            frameOwner.getFrame().setEnabled(true);
                        }
                        if (frameOwner.getFrame().getExtendedState() == JFrame.ICONIFIED){
                            //System.err.println("JFRAME.NORMAL [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                            frameOwner.getFrame().setExtendedState(JFrame.NORMAL);
                            //System.err.println("TO FRONT [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                        }//x    
                        frameOwner.getFrame().toFront();
                    //}
                }
                
                frameChild.getFrame().removeWindowListener(this);
            }
            
            @Override
            public void windowIconified(WindowEvent e) {

                //System.err.println("Entrou no windowIconified [" + frameChild.getFrame().getTitle() + "] : " + Utils.getDataHora());
                
                if (frameOwner != null) {

                    if (frameOwner.getFrame().isVisible()) {
                        
                        if (invisivelAoMinimizar) {
                            //System.err.println("Ocultou janela " + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                            frameOwner.getFrame().setVisible(false);
                        }else{
                            //System.err.println("ICONIFIED " + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                            frameOwner.getFrame().setExtendedState(JFrame.ICONIFIED);
                        }
                    }

                    ////////////////////////////////////////
                    // Oculta todas as janelas anteriores //
                    ////////////////////////////////////////

                    ModalFrameHierarchy fram = frameOwner;
                    while (fram.getModalOwner() != null) {

                        fram = fram.getModalOwner();

                        if (invisivelAoMinimizar) {
                            if (fram.getFrame().isVisible()) {
                                //System.err.println("Ocultou janela " + fram.getFrame().getTitle() + "] : " + Utils.getDataHora());
                                fram.getFrame().setVisible(false);
                            }
                        }else{
                            //System.err.println("ICONIFIED" + fram.getFrame().getTitle() + "] : " + Utils.getDataHora());
                            fram.getFrame().setExtendedState(JFrame.ICONIFIED);
                        }
                    }
                }
            }
                        
            @Override
            public void windowDeiconified(WindowEvent e) {

                //System.err.println("Entrou no windowDeiconified [" + frameChild.getFrame().getTitle() + "] : " + Utils.getDataHora());
                
                if (frameOwner != null) {

                    ArrayList<ModalFrameHierarchy> framesList = new ArrayList();
                    
                    if (!ocultarOwner){
                        framesList.add(frameOwner);
                    }

                    ModalFrameHierarchy fram = frameOwner;
                    while (fram.getModalOwner() != null) {
                        fram = fram.getModalOwner();
                        framesList.add(fram);
                    }

                    for (int x = framesList.size() - 1; x >= 0; x--) {

                        if (!framesList.get(x).getFrame().isVisible()) {
                            //System.err.println("VISIBLE TRUE " + framesList.get(x).getFrame().getTitle() + "] : " + Utils.getDataHora());
                            framesList.get(x).getFrame().setVisible(true);
                        }
                    }
                }

            }
        }); 
 
        if (frameOwner != null) {

            frameOwner.getFrame().addWindowListener(new WindowAdapter() {

                @Override
                public void windowActivated(WindowEvent e) {
                    
                    //System.err.println("Entrou no windowActivated [" + frameOwner.getFrame().getTitle() + "] : " + Utils.getDataHora());
                    
                    if (frameChild.getFrame().isShowing()) {
                        
                        if (frameChild.getFrame().getState() == JFrame.ICONIFIED) {
                           //System.err.println("Setou Normal" + frameChild.getFrame().getTitle() + "] : " + Utils.getDataHora());
                           frameChild.getFrame().setExtendedState(JFrame.NORMAL);
                        }
                        //System.err.println("Maximizou o filho" + frameChild.getFrame().getTitle() + "] : " + Utils.getDataHora());
                        frameChild.getFrame().toFront();
                        
                    } else {
                        frameOwner.getFrame().removeWindowListener(this);
                    }

                }
            });

        }
        
        if (ativarChild) {
            if (!frameChild.getFrame().isVisible()) {
                frameChild.getFrame().setVisible(true);
            }
            if (!frameChild.getFrame().isEnabled()) {
                frameChild.getFrame().setEnabled(true);
            }
        }
        
        if (bloquear) {
            try {
                new EventPump(frameChild.getFrame()).start();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
}