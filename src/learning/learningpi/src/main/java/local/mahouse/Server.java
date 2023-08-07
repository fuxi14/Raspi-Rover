/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import com.pi4j.util.Console;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Aquesta classe fa de servidor,
 * Font: https://www.digitalocean.com/community/tutorials/java-socket-programming-server-client
 * @author Marcel, pankaj
 */
public class Server extends Thread{
    //Necessari per crear el fil
    private Thread t;
    private String threadName;
    
    //Variable estàtica server
    private static ServerSocket service;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    //El port que escoltarà
    private static int port = 9876;
    private static boolean working = true;
    private static String message = "";
    //Consola de sortida
    private static final Console console = new Console();
    private static boolean canRetry = true;
    
    Server(String name) {
        threadName = name;
        console.println("Server thread created", this);
    }
    
    @Override
    public void run() {
        while(canRetry) {
            canRetry = false;
            try {
                this.on();
           } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }
    
    @Override
    public void start() {
        console.println("Started server thread", this);
        if(t == null) {//Comprovem si s'ha inicialitzat el fil i (si no) l'iniciem
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
    public void on() throws IOException, ClassNotFoundException, EOFException{
        try {
            //Creem objecte server
            service = new ServerSocket(port);
            //Si el procés es mor a causa de que el client es desconecta, el podem tornar a cridar

            //Escoltem indefinidament mentre no rebi l'ordre de sortir
            //Obrim una nova connecció amb l'objecte "socket" i esperem a que s'inicialitzi la connecció
            console.println("Waiting for client on port " + Integer.toString(port) + "...", this);
            
            socket = service.accept();
            console.println("Connection initialized, prociding to read sent info...", this);
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            while(working) {

                console.println("Waiting for input...", this);
                //Convertim l'Input Stream en un "String"
                message = (String) ois.readObject();
                //Passem el valor obtingut al fil principal
                
                
                
                console.println("Message recieved: " + message, this);
                console.println("Responding to client... ", this);
                //Responem al client:

                oos.writeObject("Hi Client! Message recieved was: " + message);

                //Tancar si és necessari
                if(message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit")) {
                    console.println("Client requested closing of the server...", this);
                    working = false;
                    Resource.word = "quit";

                } else {
                    Resource.word = message;
                }
            }
            console.println("Goodbye!", this);
            
        }
        
        catch (EOFException e) {
            canRetry = true;
            console.println("Connection with client was lost, restarting server...", this);
        }
        finally {
            //Tanquem recursos
            oos.close();
            ois.close();
            socket.close();
            service.close();
        }
    }
    
}