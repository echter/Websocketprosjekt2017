package no.ntnu.stud.websocket.implementation.experiment;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chris on 28.04.2017.
 */
public class Experimental {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {

        final int PORTNR = 2345;

        ServerSocket tjener = new ServerSocket(PORTNR);
        System.out.println("Logg for tjenersiden. N� venter vi...");
        java.net.Socket connection = tjener.accept();  // venter inntil noen tar kontakt


    /* �pner str�mmer for kommunikasjon med klientprogrammet */
        InputStream input = connection.getInputStream();
        OutputStream output = connection.getOutputStream();

        String dataIn = new Scanner(input, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        System.out.println(dataIn);


        System.out.println("Incoming...");

        Matcher get = Pattern.compile("^GET").matcher(dataIn);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(dataIn);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + DatatypeConverter
                    .printBase64Binary(
                            MessageDigest
                                    .getInstance("SHA-1")
                                    .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                            .getBytes("UTF-8")))
                    + "\r\n\r\n")
                    .getBytes("UTF-8");

            output.write(response, 0, response.length);
            System.out.println("seems ok");
        }

        while (true) {

            int clientMessage = input.read();
            System.out.println("First bit: " + clientMessage);

            if (clientMessage >= 100 && clientMessage < 200) {
                clientMessage = input.read();
                System.out.println("Length bit: " + clientMessage);
                if (clientMessage - 128 > 0 && clientMessage - 128 <= 125) {
                    int length = clientMessage - 128;
                    int[] key = new int[4];
                    int[] decoded = new int[length];
                    for (int i = 0; i < 4; i++) {
                        key[i] = input.read();
                        System.out.println("KEY: " + key[i]);
                    }
                    for (int i = 0; i < length; i++) {
                        int encoded = input.read();
                        decoded[i] = (byte) (encoded ^ key[i & 0x3]);
                    }
                    for (int i = 0; i < decoded.length; i++) {
                        System.out.println("OUT: " + decoded[i]);
                    }

                    byte[] firstByte = new byte[length + 2];
                    firstByte[0] = (byte) 0b10000001;
                    firstByte[1] = (byte) decoded.length;
                    for (int i = 2; i < decoded.length + 2; i++) {
                        firstByte[i] = (byte) decoded[i - 2];
                    }
                    output.write(firstByte);
                }
            }
        }
    }
}
