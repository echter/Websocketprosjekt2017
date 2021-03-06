package no.ntnu.stud.websocket;

import no.ntnu.stud.websocket.enums.Status;
import no.ntnu.stud.websocket.util.MultiThreadUtil;
import no.ntnu.stud.websocket.enums.OpCode;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
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
    private TimerTask myTask = new TimerTask() {
        @Override
        public void run() {
            try {
                System.out.println("SOCKET: " + socket + " STATUS " + status);
                if (ping && Status.OPEN == status) {
                    close();
                    System.out.println("CLOSED DUE TO UNRESPONSIVE PING");
                } else if (!ping && Status.OPEN == status) {
                    LocalTime currentTime = LocalTime.now();
                    String message = "" + currentTime.getHour()+ ":" + currentTime.getMinute() + ":" + currentTime.getSecond() + "\n";
                    ping(message);
                    ping = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("PING ERROR");
            }
        }
    };

    /**
     * This constructor initiates the streams used by the websocket.
     * User is not able to pass streams into the instance, instead streams from the TCP socket is used.
     * @param socket The tcp socket used by the Websocket.
     * @throws IOException Because Streams are in use.
     */
    public Websocket(Socket socket) throws IOException {
        this.socket = socket;
        input = socket.getInputStream();
        output = socket.getOutputStream();
        status = Status.CONNECTING;
        timer.schedule(myTask, 30000, 30000);
    }


    /**
     *  The opening handshake between two endpoints
     * Is intended to be compatible with HTTP-based software so that HTTP clients
     * and websocket clients may use the same port when commiunicating to a server.
     * This handshake is an HTTP upgrade request.
     * @throws IOException Because of streams
     * @throws InterruptedException Because of digesting message
     * @throws NoSuchAlgorithmException Because of base64
     */
    public void open() throws IOException, InterruptedException, NoSuchAlgorithmException {
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

            /**This (The method for finding acceptKey below) is more or less taken from:
             * https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java
             * And is a great way of generating the accept key for the handshake.
             */
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
     * Listens for messages between endpoints as long as status is open.
     * @throws IOException Because of streams in recieveFrame
     */
    public void listen()throws IOException{
        while (status == Status.OPEN) {
            recieveFrame();
        }
    }
    public void message() throws IOException {
        int currentBit = input.read();
        System.out.println("Length bit: " + currentBit);
        int length = currentBit - OVERFLOW_ADJUSTMENT;
        int[] decoded = decodeMessage(length);
        if (decoded != null) {
            writeMessage(decoded, length, OpCode.TEXTMESSAGE.getValue());
        }
    }
    /**
     *  Handles messages between endpoints.
     *  Supports text frames, close frames, pong frames and empty frames.
     *  
     * @throws IOException The outputsteams may throw exception
     */
    public void recieveFrame()throws IOException{
        // Reads first byte in message
        try {
            int currentBit = input.read();
            //Normal text message
            if (currentBit == OpCode.TEXTMESSAGE.getValue()) {
               message();
            } else if (currentBit == OpCode.CLOSE.getValue()) {
                status = Status.CLOSING;
                close();
            } else if (currentBit == OpCode.PONG.getValue()) {
                ping = false;
                System.out.println("PONG RECIEVED");
                currentBit = input.read();
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
                }
            }
            /**
             * The catch gets invoked if the client is no longer sending anything,
             * this happens if the client loses internet connection or similar problems.
             * Any other kind of disconnect should be discovered by the ping function.
             */
        } catch (Exception e){
            System.out.println("LOST CONNECTION... SOCKET CLOSING...");
            socket.close();
            status = Status.CLOSED;
        }
        System.out.println("Completed");
    }

    /**
     * This function takes only in the length of the message to be decoded. This is due to the fact that the way we implemented
     * our reader (input.read()) means that it will only read one byte at a time. This means that it doesnt need to take in the
     * message itself due to it being able to read as it runs. The way the decoding works it that it takes the four bytes after
     * the length indicator and uses them to decode all the other bytes after it. The keys change between each run and so do the
     * byte values of the coded bytes. But together they always give the correct value when the algorithm is applied. This algorithm
     * is inspired by the one at:
     * https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java.
     * @param length
     * @return decoded message
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
            //for (int i = 0; i < decoded.length; i++) {
                //System.out.println("OUT: " + decoded[i]);
            //}
            return decoded;
        }
        return null;
    }

    /**
     * This function takes in a decoded message, the length of the decoded message and a frame opcode.
     * It then combines them all in a byte array, opcode first, then the length and then the unmasked bytes.
     * Then it sends it all as a single message to the output stream.
     * The output stream will send it as a TCP frame to the client, the type of frame is decided by the opcode.
     * The payload will be the decoded bytes.
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
     * This function takes in a decoded message, the length of the decoded message and a frame opcode.
     * It then combines them all in a byte array, opcode first, then the length and then the unmasked bytes.
     * Then it sends it all as a single message to the output stream.
     * The output stream will send it as a TCP frame to the client, the type of frame is decided by the opcode.
     * The payload will be the decoded bytes.
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
     * Is a control frame
     * Closes every socket which is no longer active. Changes the readystate of this Websocket instance to closed
     * @throws IOException Because of socket tcp I/O operations
     */
    public void close() throws IOException {
        System.out.println("Closing socket: " + socket);
        if (MultiThreadUtil.removeSocket(socket)) {
            socket.close();
            status = Status.CLOSED;
        }
    }

    /**
     * Heartbeat control frame.
     * A ping is  meant to verify that the remote endpoint is still responsive.
     * This ping may contain application data.
     * @param text The text contents of Websocket message
     * @throws IOException Because of streams
     */
    public void ping(String text) throws IOException {
        System.out.println("PING SENT");
        if (text != null) {
            byte[] bytes = text.getBytes();
            writeMessage(bytes, bytes.length, OpCode.PING.getValue());
        } else {
            System.out.println("PING TEXT CAN'T BE NULL");
        }
    }

    /**
     * Ping in case of empty application data
     * Heartbeat control frame.
     * A ping is  meant to verify that the remote endpoint is still responsive
     * @throws IOException
     */
    public void ping() throws IOException {
        System.out.println("PING SENT");
        byte opCode = (byte) OpCode.PING.getValue();
        byte noText = (byte) 0b0000000;
        byte[] response = {opCode, noText};
        socket.getOutputStream().write(response);
    }

    /**
     * Readystate is importan for control over when to stop recieving new messages.
     * @return the status of the Websocket (Connecting, Open, Closing, Closed)
     */
    public Status getStatus() {
        return status;
    }
}

/* SOURCES

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


