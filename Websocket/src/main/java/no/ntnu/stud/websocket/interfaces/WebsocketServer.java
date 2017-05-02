package no.ntnu.stud.websocket.interfaces;

import no.ntnu.stud.websocket.Websocket;

/**
 * Created by Chris on 02.05.2017.
 */
public interface WebsocketServer extends Runnable{
    Websocket getWebsocket();
}
