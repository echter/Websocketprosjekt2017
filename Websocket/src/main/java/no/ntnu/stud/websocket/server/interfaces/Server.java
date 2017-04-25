package no.ntnu.stud.websocket.server.interfaces;

import no.ntnu.stud.websocket.server.interfaces.functional.Listener;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Server {
    void onConnection(Connection connection, Listener listener);
    void close();
}
