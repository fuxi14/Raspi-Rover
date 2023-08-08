package local.mahouse.rovercontroller;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Singleton {

    private static InetAddress host;
    private static Socket socket;
    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;
    private static boolean connected = false;
    private static boolean bufferEmpty = true;
    private static boolean stringBufferEmpty = true;
    public static ArrayList<String> messages = new ArrayList<String>();
    private static String addr;
    public static String mMessage;
    static Exception statConnect = null;

    static byte[] sendData = new byte[4]; //Data that we send to the server
    private static byte[] recievedData = new byte[4]; //Data that we recieve from the server

    static Object lock = new Object(); //Use to notify the main thread to continue



    //Creem un fil que connecta amb el server i escolta missatges que vénen del servidor
    public static final Runnable listener = new Runnable() {

        //Algunes variables del fil
        byte[] localData;
        @Override
        public void run() {
            //Connecció
            //Sé que no gestiono els errors, és expressament
            synchronized (Singleton.lock) {
                try {
                    host = InetAddress.getByName(addr);
                    socket = new Socket(host.getHostName(), 9876);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    connected = true;
                    Singleton.statConnect = null; //Ens assegurem que no reportem cap error
                } catch (UnknownHostException e) {
                    Singleton.statConnect = e; //Per enviar l'error a la MainActivity
                } catch (ConnectException e) {
                    Singleton.statConnect = e;
                } catch (IOException e) {
                    Singleton.statConnect = e;
                } finally {
                    System.out.println("[Thread] And we notify main thread");
                    Singleton.lock.notify();
                }

            }

            //Escolta
            while(true) {
                try {

                    //We read the data
                    localData = (byte[]) ois.readObject();
                    Singleton.setRecievedData(localData);
                    //Processem les dades
                    if(localData[0] == 0x04) {
                        if(localData[1] != (byte) 0xEE) {//Si no hi ha hagut un error en el server
                            Singleton.setBufferEmpty(false);
                        }
                    }
                    /*
                    //DEPRECATED FOR NOW
                    mMessage = ois.readObject().toString();
                    Singleton.messages.add(mMessage); //Afegim missatges
                    System.out.println("[Listener] added message: \"" + mMessage + "\"");
                    */
                    if(Singleton.isBufferEmpty()) {
                        Singleton.setBufferEmpty(false);
                    }


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                    break;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final Singleton instance = new Singleton();

    // Private constructor prevents instantiation from other classes
    private Singleton() {}


    //--------------------------- START OF GETTERS AND SETTERS -----------------------------
    public static Singleton getInstance() {
        return instance;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static void setConnected(boolean connected) {
        Singleton.connected = connected;
    }

    public static boolean isBufferEmpty() {
        return bufferEmpty;
    }

    public static void setBufferEmpty(boolean bufferEmpty) {
        Singleton.bufferEmpty = bufferEmpty;
    }


    public static byte[] getRecievedData() {
        return recievedData;
    }

    public static void setRecievedData(byte[] recievedData) {
        Singleton.recievedData = recievedData;
    }

    public static boolean isStringBufferEmpty() {
        return stringBufferEmpty;
    }

    public static void setStringBufferEmpty(boolean stringBufferEmpty) {
        Singleton.stringBufferEmpty = stringBufferEmpty;
    }

    //-------------------------- END OF GETTERS AND SETTERS -------------------------------


    public Exception connect(String addr) {

        this.addr = addr;

        //Start listener thread
        new Thread(listener, "listener").start();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return statConnect;



    }
    public static void disconnect() throws IOException {
        if (oos != null && ois != null && socket != null) { //No fem res si no s'han inicialitzat
            oos.close();
            ois.close();
            socket.close();

        }
        connected = false;
    }

    //We send a STRING message
    public static boolean sendIt(String message) {
        if(isConnected()) {
            statConnect = null;

            try {

                //La xarxa s'ha de fer servir en un fil different
                new Thread(() -> {
                    try {
                        oos.writeObject(message);
                    } catch (IOException e) {
                        statConnect = e;
                    }
                }).start();

                //Si ha fallat l'enviament
                if (statConnect != null) {
                    throw statConnect;
                }
                return true;


            } catch (IOException e) {
                return false;


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        return false;
    }

    //Send BYTE ARRAY to server
    public static boolean sendIt(int[] data) {
        if(isConnected()) {
            statConnect = null;
            try {

                //La xarxa s'ha de fer servir en un fil different
                new Thread(() -> {
                    try {
                        oos.writeObject(data);
                        oos.reset(); //All it took to fucking solve the bug that only sent the same message
                    } catch (IOException e) {
                        statConnect = e;
                    }
                }).start();

                //Si ha fallat l'enviament
                if (statConnect != null) {
                    throw statConnect;
                }
                return true;


            } catch (IOException e) {
                return false;


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

}
