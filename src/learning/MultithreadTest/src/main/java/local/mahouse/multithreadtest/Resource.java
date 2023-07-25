/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse.multithreadtest;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author nuria
 */
public class Resource {
    /*Queue<String> Word = new LinkedList<>();
    
    public synchronized void addStrings(List<String> words){//for reader
    Word.addAll(words);
    notify(); //Notifica al fil que l'ha cridat que pot continuar
  }//

  public synchronized String getString(){//for workers
    while(Word.isEmpty())
       try{ wait();}
       catch(InterruptedException e){}
    return Word.remove();
  }*/
    
    public static volatile String word = "";
    public static volatile boolean canRetry = true;
}
