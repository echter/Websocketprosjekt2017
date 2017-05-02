package no.ntnu.stud.websocket.implementation;

import no.ntnu.stud.websocket.http.Status;
import no.ntnu.stud.websocket.util.MultiThreadUtil;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Chris on 28.04.2017.
 */
public class Websocket implements Runnable{
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private Status status;
    public Websocket(Socket socket)throws IOException{
        this.socket = socket;
        input = socket.getInputStream();
        output = socket.getOutputStream();
        status = Status.CONNECTING;
    }
    public void onOpen(InputStream input, OutputStream output)throws IOException,InterruptedException, NoSuchAlgorithmException{
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
                    + "Sec-Websocket-Accept: "
                    + DatatypeConverter
                    .printBase64Binary(
                            MessageDigest
                                    .getInstance("SHA-1")
                                    .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                            .getBytes("UTF-8")))
                    + "\r\n\r\n")
                    .getBytes("UTF-8");
            output.write(response, 0, response.length);
            status = Status.OPEN;
            System.out.println("Ok...");
        }
    }

    public void onOpen(String dataIn, OutputStream output)throws IOException,InterruptedException, NoSuchAlgorithmException{
        //String dataIn = new Scanner(input, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        System.out.println(dataIn);
        System.out.println("Incoming...");
        Matcher get = Pattern.compile("^GET").matcher(dataIn);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(dataIn);
            boolean foundMatch = match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-Websocket-Accept: "
                    + DatatypeConverter
                    .printBase64Binary(
                            MessageDigest
                                    .getInstance("SHA-1")
                                    .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                            .getBytes("UTF-8")))
                    + "\r\n\r\n")
                    .getBytes("UTF-8");
            output.write(response, 0, response.length);
            status = Status.OPEN;
            System.out.println("Ok...");
        }
    }
    public void onMessage(InputStream input,int len)throws IOException, InterruptedException,NoSuchAlgorithmException{
        int opcode = 0b10000001;
        while(status != Status.CLOSED){
            // Reads first byte in message
            int currentBit = input.read();
            System.out.println("First bit: " + currentBit);
            if (currentBit == len) {
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

                    writeMessage(decoded,length,opcode);
                }
            } else if (currentBit == 136){
                status = Status.CLOSING;
                onClose();
            }

        }
        System.out.println("Completed");
    }
    private void writeMessage(int[] decoded, int length,int opcode)throws IOException{
        byte[] firstByte = new byte[length + 2];
        firstByte[0] = (byte) opcode;
        firstByte[1] = (byte) decoded.length;
        for (int i = 2; i < decoded.length+2; i++) {
            firstByte[i] = (byte) decoded[i-2];
        }
        for (Socket s: MultiThreadUtil.getSockets()) {
            s.getOutputStream().write(firstByte);
        }
    }
    public void onClose()throws IOException{
        System.out.println("Closing socket: " + socket);
        if(MultiThreadUtil.removeSocket(socket)){
            socket.close();
            status = Status.CLOSED;
        }
    }
    public void ping(){

    }
    public void pong(){

    }
    public Status getStatus(){
        return status;
    }
    @Override
    public void run(){
        try {
            int frame = 129;
            System.out.println("Log to server. Waiting....");
            onOpen(input,output);
            onMessage(input,frame);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}


