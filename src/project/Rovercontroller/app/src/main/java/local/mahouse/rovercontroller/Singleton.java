package local.mahouse.rovercontroller;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Singleton {

    static InetAddress host;
    static Socket socket;
    static ObjectOutputStream oos = null;
    static ObjectInputStream ois = null;
    static boolean connected = false;
    private static final Singleton instance = new Singleton();

    // Private constructor prevents instantiation from other classes
    private Singleton() {

    }

    public static Singleton getInstance() {
        return instance;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static void connect(View view, String addr) {
        Snackbar.make(view, "IP Address inserted is: " + addr,
                        Snackbar.LENGTH_LONG)
                //.setAction("Action", null)
                .show();

        if(connected == false) {
            connected = true;
        }else{
            connected = false;
        }

    }
}
