package no.ntnu.stud.websocket.interfaces;

import no.ntnu.stud.websocket.interfaces.functionalinterfaces.ActionListener;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Server {
    void onConnection(Connection connection, ActionListener actionListener);

}
