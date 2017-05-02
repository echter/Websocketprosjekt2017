package no.ntnu.stud.websocket.util;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by EliasBrattli on 01/05/2017.
 */
public final class MultiThreadUtil {
    static ArrayList<Socket> sockets = new ArrayList<>();
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
}
