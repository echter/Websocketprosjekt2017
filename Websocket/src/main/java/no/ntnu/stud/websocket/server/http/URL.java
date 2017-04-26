package no.ntnu.stud.websocket.server.http;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public final class URL {
    private String protocol;
    private String host;
    private int port;

    public URL (String protocol, String host, int port){
        this.protocol = protocol;
        this.host = host;
    }
}
