package no.ntnu.stud.websocket;

import no.ntnu.stud.websocket.implementation.Websocket;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by EliasBrattli on 25/04/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            Websocket websocket = new Websocket(2345);
            websocket.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
