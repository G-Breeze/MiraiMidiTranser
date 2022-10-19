package org.miditranser.data.midi.message;

public class AfterTouchChannelMessage extends HasMidiTicks {

    public AfterTouchChannelMessage(long deltaTime, long markedTicks, byte channel) {
        this.deltaTime = deltaTime;
        this.channel = channel;
        this.markTicks = markedTicks;
    }

    private final byte channel;

    public byte getChannel() {
        return channel;
    }
}
