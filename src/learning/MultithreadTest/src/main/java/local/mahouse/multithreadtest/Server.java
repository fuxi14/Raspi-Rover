/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse.multithreadtest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Aquesta classe fa de servidor,
 * Font: https://www.digitalocean.com/community/tutorials/java-socket-programming-server-client
 * @author nuria, pankaj
 */
public class Server {
    
    //Variable estàtica server
    private static ServerSocket service;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    //El port que escoltarà
    private static int port = 9876;
    private static boolean working = true;
    private static String message = "";
    
    
    
    
    public void run() throws IOException, ClassNotFoundException{
        //Creem objecte server
        service = new ServerSocket(port);
        //Escoltem indefinidament mentre no rebi l'ordre de sortir
        while(working) {
            System.out.println("[Server] Waiting for client on port " + Integer.toString(port) + "...");
            //Obrim una nova connecció amb l'objecte "socket" i esperem a que s'inicialitzi la connecció
            socket = service.accept();
            System.out.println("[Server] Connection initialized, prociding to read sent info...");
            ois = new ObjectInputStream(socket.getInputStream());
            //Convertim l'Input Stream en un "String"
            message = (String) ois.readObject();
            System.out.println("[Server] Message recieved: " + message);
            System.out.println("[Server] Responding to client... ");
            //Responem al client:
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("Hi Client! Message recieved was: " + message);
            
            //Tanquem recursos
            oos.close();
            ois.close();
            socket.close();
            
            //Tancar si és necessari
            if(message.equalsIgnoreCase("exit")) {
                System.out.println("[Server] Client requested closing of the server...");
                working = false;
            }
        }
        System.out.println("[Server] Goodbye!");
        service.close();
    }
    
}
