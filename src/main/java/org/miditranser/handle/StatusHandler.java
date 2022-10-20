package org.miditranser.handle;

import org.miditranser.data.midi.message.*;

import static org.miditranser.Utils.rest;
import static org.miditranser.Utils.toHex;

public class StatusHandler extends AbstractHandler {

    private final byte[] hexData;
    private final long deltaTime;
    private final long markedTicks;
    private final int midiStatus;

    public StatusHandler(int midiStatus, byte[] hexData, long deltaTime, long markedTicks) {
        this.midiStatus = midiStatus;
        this.hexData = hexData;
        this.deltaTime = deltaTime;
        this.markedTicks = markedTicks;
    }

    @Override
    public void handle() {
        if (midiStatus == 0xff) {
            // assume hexData use 1 byte to store length
            var type = hexData[0];
            var length = hexData[1];
            var content = rest(hexData, 2);
            if (meta != null) {
                meta.accept(new MetaMessage(type, length, content, deltaTime, markedTicks));
            }
        } else if (0x90 <= midiStatus && midiStatus <= 0x9f) {
            if (noteOn != null)
                noteOn.accept(new NoteOnMessage(hexData[1], hexData[1], (byte) (midiStatus & 0xf), deltaTime, markedTicks));
        } else if (0x80 <= midiStatus && midiStatus <= 0x8f) {
            if (noteOff != null)
                noteOff.accept(new NoteOffMessage(hexData[1], hexData[2], (byte) (midiStatus & 0xf), deltaTime, markedTicks));
        } else if (0xe0 <= midiStatus && midiStatus <= 0xef) {
            if (glissando != null)
                glissando.accept(new GlissandoMessage(deltaTime, hexData[0], hexData[1], (byte) (midiStatus & 0xf), markedTicks));
        } else if (0xc0 <= midiStatus && midiStatus <= 0xcf) {
            if (programChange != null)
                programChange.accept(new ProgramChangeMessage(hexData[0], (byte) (midiStatus & 0xf), deltaTime, markedTicks));
        } else if (0xf0 == midiStatus) {
            // assume hexData use 1 byte to store length
            var length = hexData[0];
            var content = rest(hexData);
            if (systemCode != null)
                systemCode.accept(new SystemCodeMessage(deltaTime, length, content, markedTicks));
        } else if (0xa0 <= midiStatus && midiStatus <= 0xaf) {
            if (afterTouch != null)
                afterTouch.accept(new AfterTouchMessage(hexData[1], hexData[2], (byte) (midiStatus & 0xf), deltaTime, markedTicks));
        } else if (0xb0 <= midiStatus && midiStatus <= 0xbf) {
            if (controller != null)
                controller.accept(new ControllerMessage(hexData[0], (byte) (midiStatus & 0xf), hexData[1], deltaTime, markedTicks));
        } else if (0xd0 <= midiStatus && midiStatus <= 0xdf) {
            if (afterTouchChannel != null)
                afterTouchChannel.accept(new AfterTouchChannelMessage(deltaTime, markedTicks, (byte) (midiStatus & 0xf)));
        } else throw new RuntimeException("no such handler, status: " + toHex(new byte[]{ (byte) midiStatus }));
    }
}

