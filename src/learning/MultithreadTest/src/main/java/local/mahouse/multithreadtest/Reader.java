/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse.multithreadtest;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author nuria
 */

//Creem la classe que executarà el fil
public class Reader extends Thread{
    private Thread t;
    private String threadName, word;
    Scanner input = new Scanner(System.in);
    private boolean working = true;
//Constructor
    Reader(String name) {
        threadName = name;
        System.out.println("Creating reader thread " + threadName + "...");
        
    }
 
    /*
     * @Override serveix per quan una classe filla substitueix el mètode
     * de la classe mare (run() de Reader sustitueix run() de Thread
     */
    @Override
    public void run(){
        System.out.printf("[%s] running, waiting for input...\n", threadName);
        while(working) {
            
            word = input.nextLine();
            Resource.word = word;
            //System.out.printf("[%s] Introduced word: %s\n", threadName, word);
            //classe compartida
            if(Resource.word.equalsIgnoreCase("exit")) {
                working = false;
            }
            
        }
        System.out.printf("[%s] Exiting...\n", threadName);
    }
    
    @Override
    public void start() {
        System.out.println("Started thread " + threadName);
        if(t == null) {//Comprovem si s'ha inicialitzat el fil i (si no) l'iniciem
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
