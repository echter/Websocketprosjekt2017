package no.ntnu.stud.websocket.implementation;

import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

class TCPServer {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        final int PORTNR = 2345;

        ServerSocket tjener = new ServerSocket(PORTNR);
        System.out.println("Logg for tjenersiden. N� venter vi...");
        Socket forbindelse = tjener.accept();  // venter inntil noen tar kontakt



    /* �pner str�mmer for kommunikasjon med klientprogrammet */
        InputStreamReader leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
        BufferedReader leseren = new BufferedReader(leseforbindelse);
        PrintWriter skriveren = new PrintWriter(forbindelse.getOutputStream(), true);


        System.out.println("Incoming...");
        boolean connecting = true;

        while ((leseren.readLine() != null) && !Objects.equals(leseren.readLine(), "\r\n")){

            while (connecting) {

                String possibleMatch = leseren.readLine();
                System.out.println(possibleMatch);
                if (possibleMatch.contains("Sec-WebSocket-Key:")) {

                    String[] matches = possibleMatch.split(" ");
                    System.out.println(matches[1]);

                    KeyGeneration keyGen = new KeyGeneration();
                    String acceptKey = keyGen.getKey(matches[1]);
                    System.out.println(acceptKey);
                    String responsHeader = "HTTP/1.1 101 Switching Protocols\n";
                    String responsUpgrade = "Upgrade: websocket\n";
                    String responsConnection = "Connection: Upgrade\n";
                    String responsKey = "Sec-Websocket-Accept: " + acceptKey + "\r\n";
                    String respons = responsHeader + responsUpgrade + responsConnection + responsKey;
                    skriveren.println(respons);
                    System.out.println("success?");
                    connecting = false;
                }
            }

            String line = leseren.readLine();
            while (line != null) {
                System.out.println("LOOP RUN");
                byte[] payload = line.getBytes();

                for (byte bytes : payload) {
                    System.out.print(bytes);
                    skriveren.print(bytes);
                }
                skriveren.print("\r\n");
                System.out.println("SPACE");
                line = leseren.readLine();
                if (line == null){
                    while (line == null){
                        Thread.sleep(50);
                        line = leseren.readLine();
                    }
                }
            }
        }







        System.out.println("done");

        leseren.close();
        skriveren.close();
        forbindelse.close();
    }
}


//THIS IS NOT MADE BY ME! REWRITE FOR FINAL OR GIVE CREDIT! (https://websockets.wordpress.com/the-lightweight-websockets-java-server/)
class KeyGeneration {

    /**
     * KeyGen Magic string
     */
    private final static String MAGIC_KEY =
            "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    /**
     * Given a web socket key,
     *  ex: Sec-WebSocket-Key: wZp7uNNUBt0NPRqs/sdkvQ==
     *  Generate our accept key
     */
    public static String getKey(String strWebSocketKey) throws
            NoSuchAlgorithmException {

        strWebSocketKey += MAGIC_KEY;

        MessageDigest shaMD = MessageDigest.getInstance("SHA-1");
        shaMD.reset();
        shaMD.update(strWebSocketKey.getBytes());
        byte messageDigest[] = shaMD.digest();
        BASE64Encoder b64 = new BASE64Encoder();

        return b64.encode(messageDigest);

    }
}

class WriteFrame {
    /**

     * Write a A single-frame unmasked text message
     *
     * @param os
     * @param strText
     * @throws IOException
     */
    public void doWrite(OutputStream os, String strText) throws
            IOException {

        byte[] textBytes = strText.getBytes("UTF-8");
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

 /* Add start of Frame */
        bao.write((byte) textBytes.length);
        bao.write(textBytes);
        bao.flush();
        bao.close();
        os.write(bao.toByteArray(), 0, bao.size());
        os.flush();
    }
}