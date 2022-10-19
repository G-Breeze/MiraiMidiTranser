package org.miditranser.data.midi.message;

public class MetaMessage extends HasMidiTicks {
    private final byte type;
    private final long length;
    private final byte[] data;

    public byte getType() {
        return type;
    }

    public long getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public MetaMessage(byte type, long length, byte[] data, long deltaTime, long markedTicks) {
        this.type = type;
        this.length = length;
        this.data = data;
        this.deltaTime = deltaTime;
        this.markTicks = markedTicks;
    }
}
