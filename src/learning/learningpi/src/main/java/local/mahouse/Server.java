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
    
    /*
    * Documentació (F*cking again per cagar-la)
    * Modes del rover (enviat amb l'array)
    * Index:
    * 0 = mode: 0x00 = Res | 0x01 = Control Manual | 0x02 = Control Automàtic (seguir línea) | 0x03 = Prova amb text | 0x04 = Comunicació server - client | 0x05 = Comunicació Client - Server
    * 1 (0x01) = direcció/gir: 0x00 = parar | 0x01 = Endevant | 0x02 = Endarrere | 0x03 = Girar Esquerre | 0x04 = Girar Dreta
    * 1 (0x02) = Anar o no: 0x00 = Deixar de seguir | 0x01 = Seguir
    * 1 (0x04) = Comm Ser-Cli: 0x00 - 0x04 = Canviar icona direcció a l'aplicació de mòbil | 0xee = Hi ha hagut un error | 0xff = Tancar connecció
    * 1 (0x05) = Comm Cli-Ser: 0xff = Tancar connecció
    * 2 (0x01) Velocitat motor esquerre (0x00 - 0xFF)
    *
    * 3 (0x01) Velocitat motor dreta (0x00 - 0xFF)
    *  
    */
    private static byte[] data = new byte[4];
   
    
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
           } catch (IOException ex) { //OK Houston we've had a problem here
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
    
    public void on() throws IOException, ClassNotFoundException{
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
                data = (byte[]) ois.readObject();
                console.println("Data array recieved", this);
                
                //Processem les dades rebudes
                switch(data[0]) {
                    case 0x00: //No fer res
                        console.println("We don't do anything", this);
                        break;
                    case 0x01: //Moure manualment
                        //Processem les dades per moure manualment
                        switch(data[1]) {
                            case 0x00:
                                Resource.word = "stop";
                                data[0] = 0x04;
                                data[1] = 0x00;
                                break;
                            case 0x01:
                                Resource.word = "forward";
                                data[0] = 0x04;
                                data[1] = 0x01;
                                break;
                            case 0x02:
                                Resource.word = "reverse";
                                data[0] = 0x04;
                                data[1] = 0x02;
                                break;
                            case 0x03:
                                Resource.word = "left";
                                data[0] = 0x04;
                                data[1] = 0x03;
                                break;
                            case 0x04:
                                Resource.word = "right";
                                data[0] = 0x04;
                                data[1] = 0x04;
                                break;
                            default:
                                data[0] = 0x04;
                                data[1] = (byte) 0xEE;
                                console.println("WARNING: Movement option not reconized, not changing anything", this);
                                break;
                        }
                        //Enviem resposta
                        //Encara no, però
                        //oos.writeObject(data);
                        
                        //TODO: Processar dades de velocitat 
                        
                        break;
                    default:
                        console.println("WARNING: Verb not reconized, not doing anything", this);
                        break;
                        
                }
                
            }
                
                /*
                //For now, this is no longer in use
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
            }*/
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