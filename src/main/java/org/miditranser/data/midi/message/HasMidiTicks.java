package org.miditranser.data.midi.message;

abstract public class HasMidiTicks {
    long markTicks;
    long deltaTime;

    public long getMarkTicks() {
        return markTicks;
    }

    public long getDeltaTime() {
        return deltaTime;
    }
}
