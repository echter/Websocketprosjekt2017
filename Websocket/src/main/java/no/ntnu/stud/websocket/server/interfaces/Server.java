package no.ntnu.stud.websocket.server.interfaces;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Server {
    void onConnection(Connection connection);
    void close();
}
