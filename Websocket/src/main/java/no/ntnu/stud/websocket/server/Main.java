package no.ntnu.stud.websocket.server;

import no.ntnu.stud.websocket.server.implementation.SocketServer;
import no.ntnu.stud.websocket.server.interfaces.Connection;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public class Main {
    public static void main(String[] args) {
        SocketServer s = new SocketServer();
        // EXAMPLE
        s.onConnection(null,()->{
            System.out.println("heI");
        });
    }

}
