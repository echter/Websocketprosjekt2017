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
