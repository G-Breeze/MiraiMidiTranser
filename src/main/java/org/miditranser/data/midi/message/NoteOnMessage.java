package org.miditranser.data.midi.message;

public class NoteOnMessage extends NoteMessage {

    public NoteOnMessage(byte code, byte velocity, byte channel, long deltaTime, long markedTicks) {
        super(code, velocity, channel, deltaTime, markedTicks);
        operatorCode = (byte) 0x90;
    }
}
