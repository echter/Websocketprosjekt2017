# Websocketprosjekt2017
Websocket project

# Clone repo
```sh
git clone https://github.com/echter/Websocketprosjekt2017.git
```
# Run example Websocket server

Alternative 1:

```sh
cd Websocket\src\main\java
javac Main.java
java Main
```
or run "Main" in IDE

Then go to localhost:2346/index in browser

Alternative 2:

```sh
cd Websocket\src\main\java
javac Main.java
java Main
```
or run "Main" in IDE

Then copy your own local file path to git directory Websocketprosjekt2017 + \Websocket\Index.html and paste into browser
Ex: "C:\Users\EliasBrattli\Websocketprosjekt2017\Websocket\Index.html"

# Run clients on two different computers

Computer 1:
```sh
ipconfig
```
Note Wireless LAN-adapter wifi: IPv4 Address (ex: 10.20.200.208)

Run server and client:
```sh
cd Websocket\src\main\java
javac Main.java
java Main
```
or run "Main" in IDE

Go to localhost:2346/index in browser
You can also run multiple clients, by duplicating several tabs.

Computer 2:

Go to [Wireless LAN-adapter Wifi: IPv4 address from Computer 1]:2346/index in browser

Ex: 10.20.200.208:2346/index

# Example code
Implement the WebsocketServer interface, which has a Websocket as class member.
The example code below has a Server supporting multithreading.
```sh
public class Server implements WebsocketServer {
    private Websocket websocket;
    public Server(Websocket websocket){
        this.websocket = websocket;
    }
    public Websocket getWebsocket(){
        return websocket;
    }
    @Override
    public void open()throws Exception{
        websocket.onOpen();
    }
    @Override
    public void listen()throws Exception{
        websocket.listen();
    }
    @Override
    public void run(){
        try {
            System.out.println("Log to server. Waiting....");
            open();
            listen();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```
# Javadoc
To see javadoc, clone the repo and open apidocs/index.html in browser.
```sh
cd apidocs
index.html
```
# External sources

https://tools.ietf.org/html/rfc6455
https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API
https://websockets.wordpress.com/the-lightweight-websockets-java-server/
http://blog.honeybadger.io/building-a-simple-websockets-server-from-scratch-in-ruby/
https://www.w3.org/TR/2011/WD-websockets-20110929/
http://stackoverflow.com/questions/8125507/how-can-i-send-and-receive-websocket-messages-on-the-server-side
http://codebeautify.org/string-binary-converter
http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers#Pings_and_Pongs_The_Heartbeat_of_WebSockets
