

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.Enumeration;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Client {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(2346), 0);
        server.createContext("/test", new MyHandler());
        server.start();
    }

   static String getIpName(){
       return "localhost";
   }
    public static String getIpv4Address() throws Exception {
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
                        System.out.println(String.format("NetInterface: name [%s], ip [%s]",
                                name, addr.getHostAddress()));
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
                        "    <title>Websocket Client</title>\n" +
                        "    <!--script src=\"src/main/webapp/Client.js\"></script-->\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<script>var ws = new WebSocket(\"ws://" +getIpv4Address()+ ":2345\");\n" +
                        "ws.onopen = function (event) {\n" +
                        "    //ws.send(\"abcdef\");\n" +
                        "};\n" +
                        "ws.onmessage = function (event) {\n" +
                        "    console.log(event.data);\n" +
                        "}\n" +
                        "\n" +
                        "function sendMessage() {\n" +
                        "    ws.send(\"close\");\n" +
                        "    ws.send(\"abcdef\");\n" +
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