import no.ntnu.stud.websocket.Websocket;
import no.ntnu.stud.websocket.enums.Status;
import no.ntnu.stud.websocket.interfaces.WebsocketServer;
import no.ntnu.stud.websocket.util.MultiThreadUtil;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main class for running server
 * Created by EliasBrattli on 25/04/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            int port = 2345;
            ServerSocket serverSocket = new ServerSocket(port);
            for(;;){
                Socket socket = serverSocket.accept();
                Server server = new Server(new Websocket(socket));
                new Thread(server).start();
                MultiThreadUtil.addSocket(socket);
                for (WebsocketServer wss : MultiThreadUtil.getThreads()){
                    if(wss.getWebsocket().getStatus() == Status.CLOSED){

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
