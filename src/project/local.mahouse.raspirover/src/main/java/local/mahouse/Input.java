/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse;

import java.util.Scanner;
import com.pi4j.util.Console;

/**
 *
 * @author Marcel
 */

//Fil que controla l'entrada en el terminal
public class Input extends Thread{
    private Thread t;
    private String threadName, word;
    Scanner input = new Scanner(System.in);
    private boolean working = true;
    private static final Console console = new Console();
    
//Constructor
    Input(String name) {
        threadName = name;
        console.println("Created reader thread");
        
    }
 
    /*
     * @Override serveix per quan una classe filla substitueix el mètode
     * de la classe mare (run() de Reader sustitueix run() de Thread
     */
    @Override
    public void run(){
        console.println("running, waiting for input...\n", threadName);
        while(working) {
            
            word = input.nextLine();
            if(Resource.word.equalsIgnoreCase("quit") && !Resource.canExit) {
                console.println("Error: Exit is not allowed now, please complete task before exit is allowed", this);
            }else{
                Resource.word = word;
                //console.println("Introduced word: %s\n", word);
                
            }
            if(Resource.word.equalsIgnoreCase("quit")) {
                working = false;
            }
            
        }
        console.println("Exiting...");
    }
    
    @Override
    public void start() {
        console.println("Started thread");
        if(t == null) {//Comprovem si s'ha inicialitzat el fil i (si no) l'iniciem
            t = new Thread(this, threadName);
            t.start();
        }
    }
}