package no.ntnu.stud.websocket;

import no.ntnu.stud.websocket.implementation.WebSocket;
import no.ntnu.stud.websocket.util.MultiThreadUtil;

import java.net.ServerSocket;
import java.net.Socket;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2345);
            for(;;){
                Socket socket = serverSocket.accept();
                new Thread(new WebSocket(socket)).start();
                MultiThreadUtil.addSocket(socket);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
