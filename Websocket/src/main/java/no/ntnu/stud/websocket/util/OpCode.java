package no.ntnu.stud.websocket.util;

/**
 * Created by Chris on 02.05.2017.
 */
public enum OpCode {
    TEXTMESSAGE(0b10000001), CLOSE(0b10001000), PING(0b10001001), PONG(10001010);

    private int value;

    OpCode(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
