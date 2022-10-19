package org.miditranser.data.midi.message;

public class ProgramChangeMessage extends HasMidiTicks {
    private final byte instrument;
    private final byte channel;

    public byte getChannel() {
        return channel;
    }

    public byte getInstrument() {
        return instrument;
    }

    public ProgramChangeMessage(byte instrument, byte channel, long deltaTime, long markedTicks) {
        this.instrument = instrument;
        this.channel = channel;
        this.deltaTime = deltaTime;
        this.markTicks = markedTicks;
    }
}
