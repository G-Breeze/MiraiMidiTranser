package org.miditranser.data;

import org.miditranser.data.midi.message.HasMidiTicks;

import java.util.ArrayList;

public class NoneOccupyTicksContainer extends ArrayList<HasMidiTicks> {
    public NoneOccupyTicksContainer(HasMidiTicks message) {
        this.message = message;
    }

    public HasMidiTicks getMessage() {
        return message;
    }

    HasMidiTicks message;
}
