package org.miditranser.data.midi.message;

public class GlissandoMessage extends HasMidiTicks {
    public GlissandoMessage(long deltaTime, byte high, byte low, byte channel, long markedTicks) {
        this.deltaTime = deltaTime;
        this.high = high;
        this.low = low;
        this.channel = channel;
        this.markTicks = markedTicks;
    }

    public byte getChannel() {
        return channel;
    }

    public byte getHigh() {
        return high;
    }

    public byte getLow() {
        return low;
    }

    private final byte high;
    private final byte low;

    private final byte channel;
}
