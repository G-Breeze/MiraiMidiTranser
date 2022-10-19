package org.miditranser.data.midi.message;

public class AfterTouchMessage extends NoteMessage {
    public AfterTouchMessage(byte code, byte velocity, byte channel, long deltaTime, long markedTicks) {
        super(code, velocity, channel, deltaTime, markedTicks);

        operatorCode = (byte) 0xa0;
    }
}
