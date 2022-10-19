package org.miditranser.data;

import org.miditranser.data.midi.message.NoteMessage;

public class NotePiece extends HasNoteCode implements MidiHexData {
    int type;
    int velocity;
    byte channel;

    public long getMarkTicks() {
        return markTicks;
    }

    long markTicks;

    public long getLastingTick() {
        return lastingTick;
    }

    public void setLastingTick(long lastingTick) {
        this.lastingTick = lastingTick;
    }

    long lastingTick;

    public NotePiece(NoteMessage msg) {
        this(msg.getCode(), msg.isNoteOn() ? 0x90 : 0x80, msg.getVelocity(), msg.getChannel(), msg.getMarkTicks());
    }

    public boolean isNoteOn() {
        return type == 0x90;
    }

    public boolean isNoteOff() {
        return type == 0x80;
    }

    @Override
    public String toString() {
        var opc = isNoteOn() ? "on" : "off";
        return "{" + opc + getNoteName() + "}";
    }

    @Override
    public long getHeadTicks() {
        return markTicks;
    }

    @Override
    public long getTailTicks() {
        return markTicks;
    }

    public NotePiece(byte code, int type, int velocity, byte channel, long ticks) {
        super(code);
        this.type = type;
        this.velocity = velocity;
        this.markTicks = ticks;
        this.channel = channel;
    }

    @Override
    public boolean stackTypeIsPush() {
        return isNoteOn();
    }
}
