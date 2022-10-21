package org.miditranser.data.midi.message;

abstract public class HasMidiTicks implements HasOrder {
    long markTicks;
    long deltaTime;

    int order = 0;

    public long getMarkTicks() {
        return markTicks;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
