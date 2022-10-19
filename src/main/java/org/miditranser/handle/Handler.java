package org.miditranser.handle;

import org.miditranser.data.midi.message.*;

import java.util.function.Consumer;

public interface Handler {
    Handler handleNoteOff(Consumer<NoteOffMessage> noteOff);

    Handler handleAfterTouch(Consumer<AfterTouchMessage> afterTouch);

    Handler handleController(Consumer<ControllerMessage> controller);

    Handler handleProgramChange(Consumer<ProgramChangeMessage> programChange);

    Handler handleAfterTouchChannel(Consumer<AfterTouchChannelMessage> afterTouchChannel);

    Handler handleGlissando(Consumer<GlissandoMessage> glissando);

    Handler handleNoteOn(Consumer<NoteOnMessage> noteOn);

    Handler handleSystemCode(Consumer<SystemCodeMessage> systemCode);

    void handle();
}
