package br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao;

import java.util.*;

public class ObservableList {
    
  private Vector list;
  private Vector listeners;
    
  public ObservableList() {
    list      = new Vector();
    listeners = new Vector();
  }

  public Enumeration elements() {
    return list.elements();
  }
  
  public int totalElements(){
    return list.size();
  }

  public synchronized void addElement(Object o) {
    list.addElement(o);
    fireUpdate();
  }

  public synchronized void removeElement(Object o) {
    list.remove(o);
    fireUpdate();
  }
    
  public synchronized void replaceElementAtEnd(Object o, Object n) {
    list.removeElement(o);
    addElement(n);
  }

  public synchronized void addUpdateListener(UpdateListener l) {
    listeners.addElement(l);
  }

  public synchronized void removeUpdateListener(UpdateListener l) {
    listeners.removeElement(l);
  }

  void fireUpdate() {
    UpdateEvent event = new UpdateEvent(this);
    for (int i = 0; i < listeners.size (); ++ i)
      ((UpdateListener) listeners.elementAt (i)).updateOccurred (event);
  }
}
  
