package org.miditranser.data;

import org.miditranser.Utils;
import org.miditranser.data.midi.message.NoteOffMessage;
import org.miditranser.data.midi.message.NoteOnMessage;

import static org.miditranser.Utils.calculateDuration;

public class Note extends HasNoteCode implements FromMidiEvent {
    int onChannel;
    int offChannel;
    long onTicks;
    long offTicks;
    int onVelocity;
    int offVelocity;

    public Note(NoteOnMessage on, NoteOffMessage off) {
        this(on.getCode(), on.getMarkTicks(), off.getMarkTicks(), on.getVelocity(), off.getVelocity(), on.getChannel(), off.getChannel());
    }

    public Note(byte code, long onTicks, long offTicks, int onVelocity, int offVelocity, int onChannel, int offChannel) {
        super(code);
        this.onTicks = onTicks;
        this.offTicks = offTicks;
        this.onChannel = onChannel;
        this.offChannel = offChannel;
        this.onVelocity = onVelocity;
        this.offVelocity = offVelocity;
    }

    @Override
    public long getHeadTicks() {
        return onTicks;
    }

    @Override
    public long getTailTicks() {
        return offTicks;
    }

    public long getTicks() {
        return offTicks - onTicks;
    }

    @Override
    public String toString() {
        return "<note: " + getNoteName() + ", ticks: " + getTicks() + ", startAt: " + onTicks + ", endAt: " + offTicks + ">";
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        return getNoteName() + calculateDuration(getTicks(), cdc.division, cdc.accuracy).asMiderDurationSymbols();
    }

    //    public Note(String name, long ticks, int onVelocity, int offVelocity, int channel) {
//        this.ticks = ticks;
//        this.channel = channel;
//        this.onVelocity = onVelocity;
//        this.offVelocity = offVelocity;
//        this.name = name;
//    }

//    public Note(int code, long ticks, int onVelocity, int offVelocity, int channel) {
//        this((byte) code, ticks, onVelocity, offVelocity, channel);
//    }

//    public Note(byte code, long ticks, int onVelocity, int offVelocity, int channel) {
//        this(Utils.transferNoteName(code), ticks, onVelocity, offVelocity, channel);
//    }
}


