package no.ntnu.stud.websocket;

import no.ntnu.stud.websocket.implementation.Websocket;
import no.ntnu.stud.websocket.util.MultiThreadUtil;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2345);
            for(;;){
                Socket socket = serverSocket.accept();
                new Thread(new Websocket(socket)).start();
                MultiThreadUtil.addSocket(socket);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
