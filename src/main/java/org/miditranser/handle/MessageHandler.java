package org.miditranser.handle;

import org.miditranser.data.midi.message.*;

public class MessageHandler extends AbstractHandler {

    public MessageHandler(HasMidiTicks message) {
        this.message = message;
    }

    HasMidiTicks message;

    @Override
    public void handle() {
        if (noteOn != null && message instanceof NoteOnMessage) {
            noteOn.accept(((NoteOnMessage) message));
        } else if (noteOff != null && message instanceof NoteOffMessage) {
            noteOff.accept(((NoteOffMessage) message));
        } else if (afterTouch != null && message instanceof AfterTouchMessage) {
            afterTouch.accept(((AfterTouchMessage) message));
        } else if (meta != null && message instanceof MetaMessage) {
            meta.accept(((MetaMessage) message));
        } else if (programChange != null && message instanceof ProgramChangeMessage) {
            programChange.accept(((ProgramChangeMessage) message));
        } else if (afterTouchChannel != null && message instanceof AfterTouchChannelMessage) {
            afterTouchChannel.accept(((AfterTouchChannelMessage) message));
        } else if (controller != null && message instanceof ControllerMessage) {
            controller.accept(((ControllerMessage) message));
        } else if (systemCode != null && message instanceof SystemCodeMessage) {
            systemCode.accept(((SystemCodeMessage) message));
        } else if (glissando != null && message instanceof GlissandoMessage) {
            glissando.accept(((GlissandoMessage) message));
        } else throw new RuntimeException("no such handle method: " + message);
    }
}
