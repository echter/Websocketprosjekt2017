# Websocketprosjekt2017
Websocket project

# Clone repo
git clone https://github.com/echter/Websocketprosjekt2017.git

# Run example Websocket server

Alternative 1:

```sh
cd Websocket\src\main\java
javac Main.java
java Main
```

Then go to localhost:2346/index in browser

Alternative 2:

```sh
cd Websocket\src\main\java
javac Main.java
java Main
```
Copy your own local file path to git directory Websocketprosjekt2017 + \Websocket\Index.html and paste into browser
Ex: "C:\Users\EliasBrattli\Websocketprosjekt2017\Websocket\Index.html"

# Run clients on two different computers

Computer 1:
```sh
ipconfig
```
Note Wifi LAN-adapter wifi: IPv4 Address (ex: 10.20.200.208)

Run server:
```sh
cd Websocket\src\main\java
javac Main.java
java Main
```
Computer 2:

Go to [IPv4 address from Computer 1]:2346/index in browser
Ex: 10.20.200.208:2346/index
