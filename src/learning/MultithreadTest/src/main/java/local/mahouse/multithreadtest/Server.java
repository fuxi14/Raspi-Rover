/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse.multithreadtest;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import server.DiscoveryServer.DiscoveryServer;

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
    private static boolean discoverOn = false;
    
    public static boolean isDiscoverOn() {return discoverOn;}
    
    
    
    public void run() throws IOException, ClassNotFoundException, EOFException{
        try {
            //Iniciem el server per poder descobrir el server
            if (!discoverOn) {
                discoverOn = true;
                
                new Thread(() -> {
                    DiscoveryServer.main();
                }).start();
            }
            
            //Creem objecte server
            service = new ServerSocket(port);
            //Si el procés es mor a causa de que el client es desconecta, el podem tornar a cridar

            //Escoltem indefinidament mentre no rebi l'ordre de sortir
            //Obrim una nova connecció amb l'objecte "socket" i esperem a que s'inicialitzi la connecció
            System.out.println("[Server] Waiting for client on port " + Integer.toString(port) + "...");
            socket = service.accept();
            System.out.println("[Server] Connection initialized, prociding to read sent info...");
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            while(working) {

                System.out.println("[Server] Waiting for input...");
                //Convertim l'Input Stream en un "String"
                message = (String) ois.readObject();
                System.out.println("[Server] Message recieved: " + message);
                System.out.println("[Server] Responding to client... ");
                //Responem al client:

                oos.writeObject("Hi Client! Message recieved was: " + message);

                //Tancar si és necessari
                if(message.equalsIgnoreCase("exit")) {
                    System.out.println("[Server] Client requested closing of the server...");
                    working = false;

                }
            }
            System.out.println("[Server] Goodbye!");
            discoverOn = false;
            
        } catch (EOFException e) {
            Resource.canRetry = true;
            System.out.println("[Server] Connection with client was lost, restarting server...");
        } catch(Exception e)  {
            Resource.canRetry = true;
            e.printStackTrace();
            System.out.println("[Server] Unkwown error, restarting server...");
        } finally {
            //Tanquem recursos
            oos.close();
            ois.close();
            socket.close();
            service.close();
        }
    }
    
}
