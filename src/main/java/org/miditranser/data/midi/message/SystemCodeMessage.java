package org.miditranser.data.midi.message;

public class SystemCodeMessage extends HasMidiTicks {
    private final long length;
    private  final byte[] code;

    public long getLength() {
        return length;
    }

    public byte[] getCode() {
        return code;
    }

    public SystemCodeMessage(long deltaTime, long length, byte[] code, long markedTicks) {
        this.deltaTime = deltaTime;
        this.length = length;
        this.code = code;
        this.markTicks = markedTicks;
    }
}
