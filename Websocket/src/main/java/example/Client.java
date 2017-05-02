package example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.Enumeration;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Client {
    static String serverPortConcat = "";
    private final String path = "/index";
    private int clientPort;
    private int serverPort;
    public Client(int clientPort,int serverPort)throws IOException{
        this.clientPort = clientPort;
        this.serverPort = serverPort;
        serverPortConcat += serverPort;
        HttpServer server = HttpServer.create(new InetSocketAddress(clientPort), 0);
        server.createContext(path, new MyHandler());
        server.start();
    }
   static String getIpName(){
       return "localhost";
   }
   public String getServerPortConcat(){
       return serverPortConcat;
   }
   public int getServerPort() {
        return serverPort;
    }
    public int getClientPort(){
       return clientPort;
    }
   static String getIpv4Address() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            // drop inactive
            if (!networkInterface.isUp())
                continue;
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if(addr instanceof Inet4Address ) {
                    if(!networkInterface.isLoopback() && !networkInterface.isVirtual() && !networkInterface.isPointToPoint() ) {
                        String name = networkInterface.getDisplayName();
                        if(!name.contains("Ethernet")&&!name.contains("Virtualbox")) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        }
        return null;
    }
    private static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                String response = "<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <title>Websocket example.Client</title>\n" +
                        "    <!--script src=\"src/main/webapp/example.Client.js\"></script-->\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<script>var ws = new WebSocket(\"ws://" +getIpv4Address()+ ":"+ serverPortConcat +"\");\n" +
                        "ws.onopen = function (event) {\n" +
                        "    //ws.send(\"abcdef\");\n" +
                        "};\n" +
                        "ws.onmessage = function (event) {\n" +
                        "    console.log(event.data);\n" +
                        "    var div = document.createElement('div');\n" +
                        "    div.innerHTML = \"<div> \" + event.data + \" </div>\";\n" +
                        "    document.body.appendChild(div);\n" +
                        "}\n" +
                        "\n" +
                        "function sendMessage() {\n" +
                        "    ws.send(\"This is a message\");\n" +
                        "}\n" +
                        "function closeButtonFunction() {\n" +
                        "    ws.close();\n" +
                        "}\n" +
                        "\n" +
                        "document.close(function () {\n" +
                        "    ws.close();\n" +
                        "})</script>\n" +
                        "<button class=\"button\" id=\"button\" onclick=\"sendMessage()\">Press me</button>\n" +
                        "<button class=\"close\" id=\"close\" onclick=\"closeButtonFunction()\">Close me</button>\n" +
                        "</body>\n" +
                        "</html>";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}