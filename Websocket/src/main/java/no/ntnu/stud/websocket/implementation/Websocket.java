package no.ntnu.stud.websocket.implementation;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Chris on 28.04.2017.
 */
public class Websocket {
    private final int PORT;
    private ServerSocket serverSocket;
    private Socket socket;
    public Websocket(int port){
        PORT = port;
    }
    private void compileMessage(InputStream input, OutputStream output)throws IOException,InterruptedException, NoSuchAlgorithmException{
        String dataIn = new Scanner(input, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        System.out.println(dataIn);
        System.out.println("Incoming...");
        Matcher get = Pattern.compile("^GET").matcher(dataIn);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(dataIn);
            boolean foundMatch = match.find();
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
            System.out.println("Ok...");
        }
    }
    public void connect()throws IOException, InterruptedException,NoSuchAlgorithmException{
        serverSocket = new ServerSocket(PORT);
        System.out.println("Logg for tjenersiden. Nå venter vi...");
        socket = serverSocket.accept();
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        compileMessage(input,output);
        decodeMessage(input,output);
    }
    private void decodeMessage(InputStream input,OutputStream output)throws IOException, InterruptedException,NoSuchAlgorithmException{
        while(true){
            // Reads first byte in message
            int currentBit = input.read();
            System.out.println("First bit: " + currentBit);
            if (currentBit > 100 && currentBit < 200) {
                currentBit = input.read();
                System.out.println("Length bit: " + currentBit);
                int length = currentBit - 128;
                if (length > 0 && length <= 125) {
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


