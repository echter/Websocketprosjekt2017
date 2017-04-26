package no.ntnu.stud.websocket.server.interfaces;

import no.ntnu.stud.websocket.server.interfaces.functionalinterfaces.ActionListener;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Server {
    void onConnection(Connection connection, ActionListener actionListener);
    void onOpen(ActionListener actionListener);
    void onError(ActionListener actionListener);

    void close(ActionListener actionListener);
}
