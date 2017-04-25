package no.ntnu.stud.websocket.server.interfaces;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Connection {
    void onError();
    void onClose();
    void onMessage();
}
