package no.ntnu.stud.websocket;

import no.ntnu.stud.websocket.enums.Status;
import no.ntnu.stud.websocket.util.MultiThreadUtil;
import no.ntnu.stud.websocket.enums.OpCode;

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
    private String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private final int BIT_ADJUSTMENT = 128;
    private final int KEY_LEN = 4;
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
            String responseHeader = "HTTP/1.1 101 Switching Protocols\n";
            String responseUpgrade = "Upgrade: websocket\n";
            String responseConnection = "Connection: Upgrade\n";
            String acceptKey = DatatypeConverter.printBase64Binary(MessageDigest.getInstance("SHA-1").digest((match.group(1) + magicString)
                    .getBytes("UTF-8")));
            String responseKey = "Sec-Websocket-Accept: " + acceptKey + "\r\n\r\n";
            System.out.println(acceptKey);
            byte[] response = (responseHeader + responseUpgrade + responseConnection + responseKey).getBytes();
            output.write(response, 0, response.length);
            status = Status.OPEN;
            System.out.println("Ok...");
        }
    }

    public void onMessage(InputStream input)throws IOException, InterruptedException,NoSuchAlgorithmException{

        while(status != Status.CLOSED){
            // Reads first byte in message
            int currentBit = input.read();
            System.out.println("First bit: " + currentBit);
            if (currentBit == OpCode.TEXTMESSAGE.getValue()) {
                currentBit = input.read();
                System.out.println("Length bit: " + currentBit);
                int length = currentBit - BIT_ADJUSTMENT;
                int[]decoded = decodeMessage(input,length);
                if(decoded != null) {
                    writeMessage(decoded,length, OpCode.TEXTMESSAGE.getValue());
                }
            } else if (currentBit == OpCode.CLOSE.getValue()){
                status = Status.CLOSING;
                onClose();
            }
        }
        System.out.println("Completed");
    }

    private int[] decodeMessage(InputStream input, int length)throws IOException{
        if (length > 0 && length <= 125) {
            int[] key = new int[KEY_LEN];
            int[] decoded = new int[length];
            for (int i = 0; i < KEY_LEN; i++) {
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
           return decoded;
        }
        return null;
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
            System.out.println("Log to server. Waiting....");
            onOpen(input,output);
            onMessage(input);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}


