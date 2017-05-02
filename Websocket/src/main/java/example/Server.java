package example;

import no.ntnu.stud.websocket.Websocket;
import no.ntnu.stud.websocket.interfaces.WebsocketServer;

/**
 * example.Server class supporting multi threading
 * Created by EliasBrattli on 02/05/2017.
 */
public class Server implements WebsocketServer {
    private Websocket websocket;
    public Server(Websocket websocket){
        this.websocket = websocket;
    }
    public Websocket getWebsocket(){
        return websocket;
    }
    @Override
    public void run(){
        try {
            System.out.println("Log to server. Waiting....");
            websocket.onOpen();
            websocket.listen();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
