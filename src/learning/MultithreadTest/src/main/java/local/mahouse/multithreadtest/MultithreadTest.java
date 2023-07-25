/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package local.mahouse.multithreadtest;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuria
 */
public class MultithreadTest {

    public static void main(String[] args) {
        String usage = "Usage: java -jar MultithreadTest.jar [OPTION] [IP ADDRESS]\n"
                    + "[Option]:\n"
                    + "Server   Initialize progam as server\n"
                    + "Client   Initialize program as client, requires server IP address";
        if(args.length == 0) {
            System.out.println("Error: No arguments passed\n" + usage);
        }else{
            switch(args[0].toLowerCase()) {
                case "server":
                    Server server = new Server();
                    while(Resource.canRetry) {
                        Resource.canRetry = false;
                        try {
                            server.run();
                        } catch (IOException ex) {
                            Logger.getLogger(MultithreadTest.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(MultithreadTest.class.getName()).log(Level.SEVERE, null, ex);

                        }
                    }
                    break;

                case "client":
                    Client client = new Client();
                {
                    try {
                        client.run(args[1]);
                    } catch (IOException ex) {
                        Logger.getLogger(MultithreadTest.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(MultithreadTest.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultithreadTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                    break;

                default:
                    System.out.println("Error, Bad Syntax:" + usage);
                    break;
            }
        }
    
    }
}
