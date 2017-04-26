package no.ntnu.stud.websocket.server.implementation;

import no.ntnu.stud.websocket.server.interfaces.Connection;
import no.ntnu.stud.websocket.server.interfaces.Server;
import no.ntnu.stud.websocket.server.interfaces.functionalinterfaces.ConnectionListener;


/**
 * Created by EliasBrattli on 25/04/2017.
 */
public class SocketServer implements Server {
    public SocketServer(){

    }
    @Override
    public void onConnection(Connection connection, ConnectionListener connectionListener){
        connectionListener.onAction();
    }
    @Override
    public void close(){

    }

}
