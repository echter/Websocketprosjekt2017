package no.ntnu.stud.websocket.server.implementation;

/**
 * Created by EliasBrattli on 26/04/2017.
 */
public class WebSockets {
    private Socket socket;
    public void open(){
        socket.onOpen(()->{
            System.out.println("Lol");
        });
    }
}
