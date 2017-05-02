import no.ntnu.stud.websocket.Websocket;
import no.ntnu.stud.websocket.util.MultiThreadUtil;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            int port = 2345;
            ServerSocket serverSocket = new ServerSocket(port);
            for(;;){
                Socket socket = serverSocket.accept();
                new Thread(new Websocket(socket)).start();
                MultiThreadUtil.addSocket(socket);
                for (Runnable t:MultiThreadUtil.getThreads()){
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
