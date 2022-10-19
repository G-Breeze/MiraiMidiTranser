package org.miditranser.data;

import org.miditranser.Utils;

public abstract class HasNoteCode implements FromMidiEvent {
    byte code;

    HasNoteCode(byte code) {
        this.code = code;
    }

    public String getNoteName() {
        return Utils.transferNoteName(code);
    }

    public int getNoteOctave() {
        return code / 12 - 1;
    }
}
