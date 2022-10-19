package org.miditranser.data;

public class CheckEventResult {
    // for note
    boolean noteCodeSameFlag = true;
    boolean noteChannelSameFlag = true;

    public boolean isNoteClosed() {
        return noteClosed;
    }

    public void setNoteClosed(boolean noteClosed) {
        this.noteClosed = noteClosed;
    }

    boolean noteClosed = true;
    
    // for chord
    boolean chordEndTicksSameFlag = true;
    boolean chordStartTicksSameFlag = true;
    boolean chordSizeSameFlag = true;

    public boolean isChordOnChannelsSameFlag() {
        return chordOnChannelsSameFlag;
    }

    public void setChordOnChannelsSameFlag(boolean chordOnChannelsSameFlag) {
        this.chordOnChannelsSameFlag = chordOnChannelsSameFlag;
    }

    boolean chordOnChannelsSameFlag = true;

    public boolean isChordOffChannelsSameFlag() {
        return chordOffChannelsSameFlag;
    }

    public void setChordOffChannelsSameFlag(boolean chordOffChannelsSameFlag) {
        this.chordOffChannelsSameFlag = chordOffChannelsSameFlag;
    }

    boolean chordOffChannelsSameFlag = true;

    public boolean isNoteCodeSameFlag() {
        return noteCodeSameFlag;
    }

    public void setNoteCodeSameFlag(boolean noteCodeSameFlag) {
        this.noteCodeSameFlag = noteCodeSameFlag;
    }

    public boolean isNoteChannelSameFlag() {
        return noteChannelSameFlag;
    }

    public void setNoteChannelSameFlag(boolean noteChannelSameFlag) {
        this.noteChannelSameFlag = noteChannelSameFlag;
    }

    public boolean isChordEndTicksSameFlag() {
        return chordEndTicksSameFlag;
    }

    public void setChordEndTicksSameFlag(boolean chordEndTicksSameFlag) {
        this.chordEndTicksSameFlag = chordEndTicksSameFlag;
    }

    public boolean isChordStartTicksSameFlag() {
        return chordStartTicksSameFlag;
    }

    public void setChordStartTicksSameFlag(boolean chordStartTicksSameFlag) {
        this.chordStartTicksSameFlag = chordStartTicksSameFlag;
    }

    public boolean isChordSizeSameFlag() {
        return chordSizeSameFlag;
    }

    public void setChordSizeSameFlag(boolean chordSizeSameFlag) {
        this.chordSizeSameFlag = chordSizeSameFlag;
    }

    public boolean isChordAllOnFlag() {
        return chordAllOnFlag;
    }

    public void setChordAllOnFlag(boolean chordAllOnFlag) {
        this.chordAllOnFlag = chordAllOnFlag;
    }

    public boolean isChordAllOffFlag() {
        return chordAllOffFlag;
    }

    public void setChordAllOffFlag(boolean chordAllOffFlag) {
        this.chordAllOffFlag = chordAllOffFlag;
    }

    boolean chordAllOnFlag = true;
    boolean chordAllOffFlag = true;
}
