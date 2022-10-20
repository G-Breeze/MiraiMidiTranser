package org.miditranser.data.midi.message;

import org.miditranser.data.Note;

import static org.miditranser.Utils.toHex;
import static org.miditranser.Utils.transferNoteName;

abstract public class NoteMessage extends HasMidiTicks {
    private byte code;

    private byte velocity;

    byte operatorCode;

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public byte getVelocity() {
        return velocity;
    }

    public void setVelocity(byte velocity) {
        this.velocity = velocity;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    private byte channel;

    public String simpleToString() {
        String name;

        if (isNoteOn()) name = "on";
        else if (isNoteOff()) name = "off";
        else if (isAfterTouch()) name = "at";
        else name = "none";

        return name + "[" + transferNoteName(code) + "|" + deltaTime + "|" + velocity + "|" + channel + "]";
    }

    @Override
    public String toString() {

        return asStackString(); // simpleToString();

        /*String className;

        if (isNoteOn()) className = "NoteOnMessage";
        else if (isNoteOff()) className = "NoteOffMessage";
        else if (isAfterTouch()) className = "AfterTouchMessage";
        else className = "NoteMessage";

        return className + '{' +
                "note=" + transferNoteName(code) +
                ", velocity=" + velocity +
                ", channel=" + channel +
                ", deltaTime=" + deltaTime +
                '}';*/
    }

    public boolean isNoteOn() {
        return operatorCode == (byte) 0x90;
    }

    public boolean isNoteOff() {
        return operatorCode == (byte) 0x80;
    }

    public boolean isAfterTouch() {
        return operatorCode == (byte) 0xa0;
    }

    public NoteOnMessage asOn() {
        return (NoteOnMessage) this;
    }

    public NoteOffMessage asOff() {
        return (NoteOffMessage) this;
    }

    public AfterTouchMessage asAfterTouch() {
        return (AfterTouchMessage) this;
    }

    public String asStackString() {
        return "[" + (isNoteOn() ? "i" : "o") + transferNoteName(getCode()) + " mark " + getMarkTicks() + "]";
    }

    public boolean codeEquals(NoteMessage msg) {
        return msg.getCode() == getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteMessage that = (NoteMessage) o;

        if (code != that.code) return false;
        if (velocity != that.velocity) return false;
        if (operatorCode != that.operatorCode) return false;
        return channel == that.channel;
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (int) velocity;
        result = 31 * result + (int) operatorCode;
        result = 31 * result + (int) channel;
        return result;
    }

    public NoteMessage(byte code, byte velocity, byte channel, long deltaTime, long markedTicks) {
        this.code = code;
        this.velocity = velocity;
        this.channel = channel;
        this.deltaTime = deltaTime;
        this.markTicks = markedTicks;
    }
}
