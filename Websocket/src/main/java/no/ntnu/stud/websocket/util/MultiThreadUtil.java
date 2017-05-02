package no.ntnu.stud.websocket.util;

import no.ntnu.stud.websocket.interfaces.WebsocketServer;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by EliasBrattli on 01/05/2017.
 */
public final class MultiThreadUtil {
    static ArrayList<Socket> sockets = new ArrayList<>();
    static ArrayList<WebsocketServer> threads = new ArrayList<>();
    private MultiThreadUtil(){}
    public static void addSocket(Socket socket){
        sockets.add(socket);
    }
    public static ArrayList<Socket> getSockets(){
        return sockets;
    }
    public static Socket getSocket(int i){
        return sockets.get(i);
    }
    public static boolean removeSocket(Socket socket) {return sockets.remove(socket);}

    public static ArrayList<WebsocketServer> getThreads() {
        return threads;
    }
}
