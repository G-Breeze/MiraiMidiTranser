package org.miditranser.handle;

import org.miditranser.data.midi.message.*;

import java.util.function.Consumer;

public abstract class AbstractHandler implements Handler {
    protected Consumer<NoteOnMessage> noteOn;
    protected Consumer<NoteOffMessage> noteOff;
    protected Consumer<AfterTouchMessage> afterTouch;
    protected Consumer<ControllerMessage> controller;
    protected Consumer<ProgramChangeMessage> programChange;
    protected Consumer<AfterTouchChannelMessage> afterTouchChannel;
    protected Consumer<GlissandoMessage> glissando;
    protected Consumer<MetaMessage> meta;
    protected Consumer<SystemCodeMessage> systemCode;

    public AbstractHandler handleNoteOff(Consumer<NoteOffMessage> noteOff) {
        this.noteOff = noteOff;
        return this;
    }

    public AbstractHandler handleAfterTouch(Consumer<AfterTouchMessage> afterTouch) {
        this.afterTouch = afterTouch;
        return this;
    }

    public AbstractHandler handleController(Consumer<ControllerMessage> controller) {
        this.controller = controller;
        return this;
    }

    public AbstractHandler handleProgramChange(Consumer<ProgramChangeMessage> programChange) {
        this.programChange = programChange;
        return this;
    }

    public AbstractHandler handleAfterTouchChannel(Consumer<AfterTouchChannelMessage> afterTouchChannel) {
        this.afterTouchChannel = afterTouchChannel;
        return this;
    }

    public AbstractHandler handleGlissando(Consumer<GlissandoMessage> glissando) {
        this.glissando = glissando;
        return this;
    }

    public AbstractHandler handleNoteOn(Consumer<NoteOnMessage> noteOn) {
        this.noteOn = noteOn;
        return this;
    }

    public AbstractHandler handleSystemCode(Consumer<SystemCodeMessage> systemCode) {
        this.systemCode = systemCode;
        return this;
    }

    public AbstractHandler handleMeta(Consumer<MetaMessage> meta) {
        this.meta = meta;
        return this;
    }
}
