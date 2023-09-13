package local.mahouse.rovercontroller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import local.mahouse.rovercontroller.ui.home.HomeFragment;

public class Singleton {

    private static InetAddress host;
    private static Socket socket;
    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;
    private static boolean connected = false;
    private static boolean bufferEmpty = true;
    private static boolean stringBufferEmpty = true;
    public static ArrayList<String> messages = new ArrayList<String>();
    private static String mAddr;
    public static String mMessage;
    static Exception statConnect = null;

    private static int[] recievedData = new int[4]; //Data that we recieve from the server

    static Object lock = new Object(); //Use to notify the main thread to continue

    //Per poder trobar l'adreça
    static volatile boolean searching;
    static volatile String foundAddress = "NONE";

    //Creem un fil que connecta amb el server i escolta missatges que vénen del servidor
    public static final Runnable listener = new Runnable() {

        //Algunes variables del fil
        int[] localData = new int[4];
        @Override
        public void run() {
            //Connecció
            //Sé que no gestiono els errors, és expressament
            synchronized (Singleton.lock) {
                try {

                    host = InetAddress.getByName(mAddr);
                    socket = new Socket(host.getHostName(), 9876);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    connected = true;
                    Singleton.statConnect = null; //Ens assegurem que no reportem cap error
                } catch (UnknownHostException e) {
                    Singleton.statConnect = e; //Per enviar l'error a la MainActivity
                    e.printStackTrace();
                } catch (ConnectException e) {
                    Singleton.statConnect = e;
                    e.printStackTrace();
                } catch (IOException e) {
                    Singleton.statConnect = e;
                    e.printStackTrace();
                } finally {
                    System.out.println("[Thread] And we notify main thread");
                    Singleton.lock.notify();
                }

            }

            //Escolta
            while(true) {
                try {

                    //We read the data
                    localData = (int[]) ois.readObject();
                    Singleton.setRecievedData(localData);
                    //Processem les dades
                    // -------------------- OBSOLET --------------------
                    /*if(localData[0] == 0x04) {
                        if(localData[1] != (byte) 0xEE) {//Si no hi ha hagut un error en el server
                            Singleton.setBufferEmpty(false);
                        }
                    }*/
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


    public static int[] getRecievedData() {
        return recievedData;
    }

    public static void setRecievedData(int[] recievedData) {
        Singleton.recievedData = recievedData;
        Singleton.setBufferEmpty(false);
    }

    public static boolean isStringBufferEmpty() {
        return stringBufferEmpty;
    }

    public static void setStringBufferEmpty(boolean stringBufferEmpty) {
        Singleton.stringBufferEmpty = stringBufferEmpty;
    }

    //-------------------------- END OF GETTERS AND SETTERS -------------------------------


    public Exception connect(String addr) {

        mAddr = addr;

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
                        } finally {
                            lock.notify();
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

    //Mètode per trovar l'adreça IP del robot
    //Basat en: https://stackoverflow.com/a/28819552
    public static void searchThread(ProgressDialog progress, Context context, int startAddr, int endAddr, int timeout) {
        int i = startAddr;
        String iIPv4;
        while(searching && i <= endAddr) {
            iIPv4 = "192.168.1." + i;
            System.out.println("TESTING: " + iIPv4);

            try {
                // First check if IP is reachable at all.
                InetAddress ip = InetAddress.getByName(iIPv4);
                if (!ip.isReachable(timeout)) {
                    //System.out.println(iIPv4 + " is not reachable...");

                    //It was FUCKING THIS that prevented this shit to work
                    progress.incrementProgressBy(1);
                } else {
                    // Address is reachable -> try connecting to socket.
                    Socket socket = new Socket();
                    SocketAddress address = new InetSocketAddress(ip, 9876);
                    socket.connect(address, timeout);
                    System.out.println(iIPv4 + " has a ServerSocket running...");
                    foundAddress = iIPv4;
                    searching = false; //Will be removed when checking is implemented

                    //ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    //ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    //We still can't use thi
                    //TODO: Implement server checking here and on Raspberry software
                /*
                //This is the packet that we sent to check whether we are talking to the robot or not
                oos.writeObject(new int[] {128, 3, 4, 5});

                try {
                    recievedData = (int[]) ois.readObject();
                } catch (ClassNotFoundException e) {
                    System.out.println(iIPv4 + " isn't what we are looking for...");
                } */


                    System.out.println("We get to where we close the thread");

                    final String theChosenOne = iIPv4;
                    Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HomeFragment.setEnterIP(theChosenOne); //We set the found IP
                            progress.dismiss();
                            //TODO: Send notification when is done searching
                            //TODO: Automatically save found IP to settings
                        }
                    });



                    socket.close();
                    //in.close();
                    //out.close();
                }



            } catch (UnknownHostException e) {
            } catch (IOException e) {
            }
            if(foundAddress != "NONE") {
                searching = false;
            } else {
                i++;
            }
        }
        System.out.println("Thread closed");
    }

    public void searchAddress(ProgressDialog progress, Context context, int timeout) {
        searching = true;

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Singleton.searchThread(progress, context, 2, 64, 1000);
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Singleton.searchThread(progress, context, 65, 128, 1000);
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Singleton.searchThread(progress, context, 129, 194, 1000);
            }
        });
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                Singleton.searchThread(progress, context, 195, 255, 1000);
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }


    //Per guardar i aplicar configuració guardada
    public String getPreferenceValue(Context context, String preferance)
    {
        String str = "ERROR";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            str = sp.getString(preferance, "");
        } catch (Exception e) {
            Toast.makeText(context, context.getText(R.string.error_generic), Toast.LENGTH_LONG).show();
            //SharedPreferences sp = context.getSharedPreferences(myPref, 0);

        } finally {
            return str;
        }
    }

    public boolean getBooleanPreferenceValue(Context context, String preferance)
    {
        boolean data = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            data = sp.getBoolean(preferance, false);
        } catch (Exception e) {
            Toast.makeText(context, context.getText(R.string.error_generic), Toast.LENGTH_LONG).show();
            //SharedPreferences sp = context.getSharedPreferences(myPref, 0);

        } finally {
            return data;
        }
    }


    public void writeToPreference(Context context, String preferance, String thePreference)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(preferance, thePreference);
        editor.commit();
    }

}
