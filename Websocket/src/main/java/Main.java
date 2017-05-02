import example.Client;
import example.Server;
import no.ntnu.stud.websocket.Websocket;
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
            int serverPort = 2345;
            int clientPort = 2346;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            new Client(clientPort,serverPort);
            // Run infinitely, listen for clients
            for(;;){
                Socket socket = serverSocket.accept(); //Accept new clients
                Server server = new Server(new Websocket(socket));
                new Thread(server).start();
                MultiThreadUtil.addSocket(socket);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
