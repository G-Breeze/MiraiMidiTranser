package org.miditranser.data;

import org.miditranser.data.midi.message.ControllerMessage;

public class Controller implements Addable {

    byte channel;
    byte number;
    byte parameter;

    public Controller(byte channel, byte number, byte parameter) {
        this.channel = channel;
        this.number = number;
        this.parameter = parameter;
    }

    public Controller(ControllerMessage cm) {
        this(cm.getChannel(), cm.getNumber(), cm.getParameters());
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        return null;
    }


}
