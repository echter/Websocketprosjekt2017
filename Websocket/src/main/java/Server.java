import no.ntnu.stud.websocket.Websocket;

/**
 * Server class supporting multi threading
 * Created by EliasBrattli on 02/05/2017.
 */
public class Server implements Runnable{
    private Websocket websocket;
    public Server(Websocket websocket){
        this.websocket = websocket;
    }
    @Override
    public void run(){
        try {
            System.out.println("Log to server. Waiting....");
            websocket.onOpen();
            websocket.onMessage();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
