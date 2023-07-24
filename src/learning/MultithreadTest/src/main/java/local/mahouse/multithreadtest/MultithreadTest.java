/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package local.mahouse.multithreadtest;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author nuria
 */
public class MultithreadTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnknownHostException, InterruptedException {
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
                    server.run();
                    break;
                case "client":
                    Client client = new Client();
                    client.run(args[1]);
                    break;
                default:
                    System.out.println("Error, Bad Syntax:" + usage);
                    break;
            }
        }
    
    }
}
