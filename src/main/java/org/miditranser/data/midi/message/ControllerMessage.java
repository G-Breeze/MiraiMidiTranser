package org.miditranser.data.midi.message;

public class ControllerMessage extends HasMidiTicks {
    private final byte number;

    public byte getNumber() {
        return number;
    }

    public byte getParameters() {
        return parameters;
    }


    private final byte parameters;


    public ControllerMessage(byte number, byte parameters, long deltaTime, long markedTicks) {
        this.number = number;
        this.parameters = parameters;
        this.deltaTime = deltaTime;
        this.markTicks = markedTicks;
    }


}
