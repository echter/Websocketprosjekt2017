package no.ntnu.stud.websocket;

import no.ntnu.stud.websocket.enums.Status;
import no.ntnu.stud.websocket.util.MultiThreadUtil;
import no.ntnu.stud.websocket.enums.OpCode;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Websocket api.
 * Uses standarized methods based on rfc6455 and https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API
 * Created by Chris on 28.04.2017.
 */
public class Websocket {
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private Status status;
    private final String MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private final int OVERFLOW_ADJUSTMENT = 128;
    private final int KEY_LEN = 4;
    private boolean ping = false;
    private Timer timer = new Timer();

    /**
     *
     * @param socket The tcp socket used by the Websocket.
     * @throws IOException Because Streams are in use.
     */
    public Websocket(Socket socket) throws IOException {
        this.socket = socket;
        input = socket.getInputStream();
        output = socket.getOutputStream();
        status = Status.CONNECTING;
        timer.schedule(myTask, 5000, 5000);
    }

    private TimerTask myTask = new TimerTask() {
        @Override
        public void run() {
            try {
                System.out.println("SOCKET: " + socket + " STATUS " + status);
                if (ping && Status.OPEN == status) {
                    onClose();
                } else if (!ping && Status.OPEN == status) {
                    Date currentTIme = new Date();
                    String message = "" + currentTIme.getHours() + ":" + currentTIme.getMinutes() + ":" + currentTIme.getSeconds() + "\n";
                    onPing(message);
                    ping = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @throws IOException Because of InputStreams
     */
    public void onOpen() throws IOException, InterruptedException, NoSuchAlgorithmException {
        String dataIn = new Scanner(input, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        System.out.println(dataIn);
        System.out.println("Incoming...");
        Matcher get = Pattern.compile("^GET").matcher(dataIn);

        //If connection is found, it will attempt the handshake
        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(dataIn);
            System.out.println("DATA IN:     " + dataIn);
            match.find();
            String responseHeader = "HTTP/1.1 101 Switching Protocols\n";
            String responseUpgrade = "Upgrade: websocket\n";
            String responseConnection = "Connection: Upgrade\n";

            //This is more or less taken from: https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java
            //And is a great way of generating the accept key for the handshake.
            String acceptKey = DatatypeConverter.printBase64Binary(MessageDigest.getInstance("SHA-1")
                    .digest((match.group(1) + MAGIC_STRING)
                            .getBytes("UTF-8")));


            String responseKey = "Sec-Websocket-Accept: " + acceptKey + "\r\n\r\n";
            System.out.println(acceptKey);

            //Converts everything to a byte[] array, this is needed for sending as everything has to be sent in the same response.
            byte[] response = (responseHeader + responseUpgrade + responseConnection + responseKey).getBytes();
            output.write(response, 0, response.length);

            //Status set to OPEN
            status = Status.OPEN;
            System.out.println("Ok...");

            //This tests the PING function, it should respons with a PONG function if successful
        }
    }


    /**
     * @throws IOException
     */
    public void onMessage()throws IOException{


        //If the connection is closed, this wont run.
        while (status != Status.CLOSED) {
            // Reads first byte in message
            int currentBit = input.read();

            //System.out.println("First bit: " + currentBit);

            if (currentBit == OpCode.TEXTMESSAGE.getValue()) {
                currentBit = input.read();
                System.out.println("Length bit: " + currentBit);
                int length = currentBit - OVERFLOW_ADJUSTMENT;
                int[] decoded = decodeMessage(length);
                if (decoded != null) {
                    writeMessage(decoded, length, OpCode.TEXTMESSAGE.getValue());
                }
            } else if (currentBit == OpCode.CLOSE.getValue()) {
                status = Status.CLOSING;
                onClose();
            } else if (currentBit == OpCode.PONG.getValue()) {
                ping = false;
                System.out.println("PONG RECIEVED");
                currentBit = input.read();
                //System.out.println("PONG bit: " + currentBit);
                int length = currentBit - OVERFLOW_ADJUSTMENT;
                if (length > 0) {
                    //System.out.println(length + " This is the length of the PONG message");
                    int[] decoded = decodeMessage(length);
                    String message = "";
                    for (int decode : decoded) {
                        message += (char) decode;
                    }
                    System.out.println(message);
                } else {
                    for (int i = 0; i < KEY_LEN; i++) {
                        input.read(); //this gets rid of the decryption keys that exist even when there is no message
                    }
                    //System.out.println("There was no message in this ping.");
                }
            }
        }
        System.out.println("Completed");
    }

    /**
     * @param length
     * @return
     * @throws IOException
     */
    private int[] decodeMessage(int length) throws IOException {
        if (length > 0 && length <= 125) {
            int[] key = new int[KEY_LEN];
            int[] decoded = new int[length];
            for (int i = 0; i < KEY_LEN; i++) {
                key[i] = input.read();
                //System.out.println("KEY: " + key[i]);
            }
            for (int i = 0; i < length; i++) {
                int encoded = input.read();
                decoded[i] = (byte) (encoded ^ key[i & 0x3]);
            }
            for (int i = 0; i < decoded.length; i++) {
                //System.out.println("OUT: " + decoded[i]);
            }
            return decoded;
        }
        return null;
    }

    /**
     * @param decoded Int values of decoded array
     * @param length
     * @param opcode
     * @throws IOException
     */
    private void writeMessage(int[] decoded, int length, int opcode) throws IOException {
        byte[] message = new byte[length + 2];
        message[0] = (byte) opcode;
        message[1] = (byte) decoded.length;
        for (int i = 2; i < decoded.length + 2; i++) {
            message[i] = (byte) decoded[i - 2];
        }
        for (Socket s : MultiThreadUtil.getSockets()) {
            s.getOutputStream().write(message);
        }
    }

    /**
     *
     * @param decoded Byte value of decoded array
     * @param length
     * @param opcode
     * @throws IOException
     */
    private void writeMessage(byte[] decoded, int length, int opcode) throws IOException {
        byte[] firstByte = new byte[length + 2];
        firstByte[0] = (byte) (opcode);
        firstByte[1] = (byte) decoded.length;
        for (int i = 2; i < decoded.length + 2; i++) {
            firstByte[i] = decoded[i - 2];
        }
        for (Socket s : MultiThreadUtil.getSockets()) {
            s.getOutputStream().write(firstByte);
        }
    }

    /**
     * @throws IOException Because of socket tcp I/O operations
     */
    public void onClose() throws IOException {
        System.out.println("Closing socket: " + socket);
        if (MultiThreadUtil.removeSocket(socket)) {
            socket.close();
            status = Status.CLOSED;
        }
    }

    public void onPing(String text) throws IOException {
        System.out.println("PING SENT");
        if (text != null) {
            byte[] bytes = text.getBytes();
            writeMessage(bytes, bytes.length, OpCode.PING.getValue());
        } else {
            System.out.println("PING TEXT CANT BE NULL");
        }
    }

    public void onPing() throws IOException {
        System.out.println("PING SENT");
        byte opCode = (byte) OpCode.PING.getValue();
        byte noText = (byte) 0b0000000;
        byte[] response = {opCode, noText};
        socket.getOutputStream().write(response);
    }

    public Status getStatus() {
        return status;
    }
}

/* KILDER

https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java
https://tools.ietf.org/html/rfc6455#section-5.5.2
https://websockets.wordpress.com/the-lightweight-websockets-java-server/
http://blog.honeybadger.io/building-a-simple-websockets-server-from-scratch-in-ruby/
https://www.w3.org/TR/2011/WD-websockets-20110929/
http://stackoverflow.com/questions/8125507/how-can-i-send-and-receive-websocket-messages-on-the-server-side
http://codebeautify.org/string-binary-converter
http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers#Pings_and_Pongs_The_Heartbeat_of_WebSockets

 */


