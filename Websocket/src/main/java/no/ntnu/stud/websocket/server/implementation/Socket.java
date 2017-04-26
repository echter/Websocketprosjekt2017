package no.ntnu.stud.websocket.server.implementation;


import no.ntnu.stud.websocket.server.interfaces.Connection;
import no.ntnu.stud.websocket.server.interfaces.functionalinterfaces.ActionListener;

/**
 * Created by EliasBrattli on 26/04/2017.
 */
public class Socket implements Connection{
    private int port;
    private String host;
    public Socket(int port,String host){
        this.port = port;
        this.host = host;
    }
    @Override
    public void onOpen(ActionListener listener){
        listener.onAction();
    }
    @Override
    public void onError(ActionListener listener){
        listener.onAction();
    }
    @Override
    public void onClose(ActionListener listener){
        listener.onAction();
    }
    @Override
    public void onMessage(ActionListener listener){
        listener.onAction();
    }
    @Override
    public int getPort(){
        return port;
    }
    @Override
    public byte[] getHostBytes(){
        return host.getBytes();
    }
    @Override
    public String getHost(){
        return host;
    }
}
