package no.ntnu.stud.websocket.interfaces;

import no.ntnu.stud.websocket.Websocket;

/**
 * Interface for the server running a Websocket
 * Created by Chris on 02.05.2017.
 */
public interface WebsocketServer extends Runnable{
    /**
     *
     * @return the server's websocket.
     */
    Websocket getWebsocket();
    void open() throws Exception;
    void listen() throws Exception;
    void close()throws Exception;
}
