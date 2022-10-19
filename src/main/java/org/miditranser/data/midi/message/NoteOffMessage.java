package org.miditranser.data.midi.message;

public class NoteOffMessage extends NoteMessage {

    public NoteOffMessage(byte code, byte velocity, byte channel, long deltaTime, long markedTicks) {
        super(code, velocity, channel, deltaTime, markedTicks);

        operatorCode = (byte) 0x80;
    }
}
