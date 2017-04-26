package no.ntnu.stud.websocket.server.interfaces;

import no.ntnu.stud.websocket.server.interfaces.functionalinterfaces.ActionListener;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Connection {
    void onOpen(ActionListener listener);
    void onError(ActionListener listener);
    void onClose(ActionListener listener);
    void onMessage(ActionListener listener);
    int getPort();
    byte[] getHostBytes();
    String getHost();
}
