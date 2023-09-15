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


public class Server {
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
    * 0 = mode: 0 = Res | 1 = Control Manual | 2 = Control Automàtic (seguir línea) | 3 = Prova amb text | 4 = Comunicació server - client | 5 = Comunicació Client - Server
    * 1 (1) = direcció/gir: sendData = parar | 1 = Endevant | 2 = Endarrere | 3 = Girar Esquerre | 4 = Girar Dreta
    * 1 (2) = Anar o no: 0 = Deixar de seguir | 1 = Seguir
    * 1 (4) = Comm Ser-Cli: 0 - 4 = Canviar icona direcció a l'aplicació de mòbil | 128 = Hi ha hagut un error | 255 = Tancar connecció
    * 1 (5) = Comm Cli-Ser: 255 = Tancar connecció
    * 2 (1) Velocitat motor esquerre (0 - 0xFF)
    *
    * 3 (1) Velocitat motor dreta (0 - 0xFF)
    *  
    */
    private int[] data;
    private int[] sendData = new int[4];
    
    
    public Runnable on = new Runnable() {
        @Override
        public void run() {
            try {
            console.println("Started server thread", this);
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
                
                //We clean and recieve new data array
                //message = (String) ois.readObject();
                data = (int[]) ois.readObject();
                console.println("Data array recieved", this);
                
                //console.println(message, this);
                //data = new Byte[4];
                //data[0] = 0;
                //Processem les dades rebudes
                switch(data[0]) {
                    case 0: //No fer res
                        console.println("We don't do anything", this);
                        break;
                    case 1: //Moure manualment
                        //Processem les dades per moure manualment
                        switch(data[1]) {
                            case 0:
                                Resource.word = "stop";
                                sendData[0] = 4;
                                sendData[1] = 0;
                                break;
                            case 1:
                                Resource.word = "forward";
                                sendData[0] = 4;
                                sendData[1] = 1;
                                break;
                            case 2:
                                Resource.word = "reverse";
                                sendData[0] = 4;
                                sendData[1] = 2;
                                break;
                            case 3:
                                Resource.word = "left";
                                sendData[0] = 4;
                                sendData[1] = 3;
                                break;
                            case 4:
                                Resource.word = "right";
                                sendData[0] = 4;
                                sendData[1] = 4;
                                break;
                            default:
                                sendData[0] = 4;
                                sendData[1] = 128;
                                console.println("WARNING: Movement option not reconized, not changing anything", this);
                                break;
                        }
                        //Enviem resposta
                        //Encara no, però
                      /*oos.writeObject(sendData);
                        oos.reset(); */
                        
                        //TODO: Processar dades de velocitat 
                        Resource.speedLeft = data[2];
                        Resource.speedRight = data[3];
                        
                        break;
                        
                    //Rebuda petició per apagar
                    case 255:
                        console.println("Off signal recieved, turning off system...");
                        Resource.word = "quit";
                        working = false;
                        break;
                    default:
                        console.println("WARNING: Verb not reconized, not doing anything", this);
                        break;
                        
                }
                data = null;
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
            
            } catch (EOFException e) {
                canRetry = true;
                console.println("Connection with client was lost, restarting server...", this);
            } catch (IOException ex) { //OK Houston we've had a problem here
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

            } finally {
            //Tanquem recursos
                try {
                    oos.close();
                    ois.close();
                    socket.close();
                    service.close();
                    console.println("Socket and ServerSocked closed");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    
    public Runnable getRunnable() {
        return on;
    }
    
}