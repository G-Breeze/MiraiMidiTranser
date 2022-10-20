package org.miditranser.data;

import org.miditranser.data.midi.message.ProgramChangeMessage;

public class ProgramChange implements Addable {

    public ProgramChange(ProgramChangeMessage pcm) {
        this(pcm.getInstrument(), pcm.getChannel());
    }

    byte instrument;
    byte channel;

    public ProgramChange(byte instrument, byte channel) {
        this.instrument = instrument;
        this.channel = channel;
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        return "{i" + channel + "=" + instrument + "}";
    }
}
