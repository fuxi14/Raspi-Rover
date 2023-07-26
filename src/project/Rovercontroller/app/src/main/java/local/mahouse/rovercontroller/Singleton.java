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

    static InetAddress host;
    static Socket socket;
    static ObjectOutputStream oos = null;
    static ObjectInputStream ois = null;
    static boolean connected = false;
    static boolean bufferEmpty = true;
    public static ArrayList<String> messages = new ArrayList<String>();
    static String addr, mMessage;
    static Exception statConnect = null;

    static Object lock = new Object(); //Use to notify the main thread to continue



    //Creem un fil que connecta amb el server i escolta missatges que vénen del servidor
    public static final Runnable listener = new Runnable() {
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
                    System.out.println("[Thread] And we notify");
                    Singleton.lock.notify();
                }

            }

            //Escolta
            while(true) {
                try {
                    mMessage = ois.readObject().toString();
                    Singleton.messages.add(mMessage); //Afegim missatges
                    System.out.println("[Listener] added message: \"" + mMessage + "\"");

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

    public Exception connect(String addr) throws NoRouteToHostException, ConnectException, IOException {

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

    //We send a message
    public static boolean sendIt(String message) {
        if(isConnected()) {
            statConnect = null;

            try {

                //La xarxa s'ha de fer servir en un fil different
                new Thread(() -> {
                    try {
                        oos.writeObject(message);
                    } catch (IOException e) {
                        Singleton.statConnect = e;
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
