package no.ntnu.stud.websocket.server.interfaces;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public interface Connection {
    void onOpen();
    void onError();
    void onClose();
    void onMessage();
    int getPort();
    byte[] getHostBytes();
    String getHost();
}
