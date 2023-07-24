/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.mahouse.multithreadtest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Aquesta classe fa de client,
 * Font: https://www.digitalocean.com/community/tutorials/java-socket-programming-server-client
 * @author nuria, pankaj
 */
public class Client {
    
    public void run(String addr) throws IOException, UnknownHostException, ClassNotFoundException, InterruptedException{
        //Aconseguim l'adreça del host
        InetAddress host = InetAddress.getByName(addr);
        Socket socket;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        boolean work = true;
        String srvResp;
        
        while(work) {
            //Inicialitzem connecció
            System.out.println("[Client] Atempting to connect to server...");
            socket = new Socket(host.getHostName(), 9876);
            
            //Esperem a que s'escrigui alguna cosa
            Reader reader = new Reader("Reader");
            reader.start();
            while(Resource.word.equals("")) {}
            
            System.out.println("[Client] Sending value of \"" + Resource.word + "\" to Server");
            //Enviem dades al server
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(Resource.word);
            
            //Esperem resposta del server
            ois = new ObjectInputStream(socket.getInputStream());
            srvResp = ois.readObject().toString();
            System.out.println("[Client] Server response was: " + srvResp);
            
            if(Resource.word.equalsIgnoreCase("exit")) {
                    System.out.println("Surt del programa");
                    work = false;
                    
            }
            
            
            
            Resource.word = "";
            socket.close();
            oos.close();
            ois.close();
        }
    }
}
