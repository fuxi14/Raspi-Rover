package local.mahouse.rovercontroller;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

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

    public static void setConnected(boolean connected) {
        Singleton.connected = connected;
    }

    public static void connect(String addr) throws NoRouteToHostException, ConnectException,IOException {
        //Connectem amb el server

        host = InetAddress.getByName(addr);
        socket = new Socket(host.getHostName(), 9876);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        connected = true;

    }
    public static void disconnect() throws IOException {
        oos.close();
        ois.close();
        socket.close();
        connected = false;

    }
}
