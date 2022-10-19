package org.miditranser.data;

import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.NoteOffMessage;
import org.miditranser.data.midi.message.NoteOnMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * closed code size
 * same velocity
 * same channel (on&off)
 * same startTicks
 * same endTicks
 */
public class StanderChord extends Chord {

    byte onVelocity;
    byte offVelocity;
    byte channel;
    long onTicks;
    long offTicks;

    public StanderChord(List<Byte> codes, byte onVelocity, byte offVelocity, byte channel, long onTicks, long offTicks) {
        super(
                codes,
                coordinate(onVelocity, codes.size()),
                coordinate(offVelocity, codes.size()),
                coordinate(channel, codes.size()),
                coordinate(channel, codes.size()),
                coordinate(onTicks, codes.size()),
                coordinate(offTicks, codes.size()));

        this.onVelocity = onVelocity;
        this.offVelocity = offVelocity;
        this.channel = channel;
        this.onTicks = onTicks;
        this.offTicks = offTicks;
    }

    public StanderChord(List<NoteOnMessage> ons, List<NoteOffMessage> offs) {
        this(
                ons.stream().map(NoteOnMessage::getCode).collect(Collectors.toList()),
                ons.get(0).getVelocity(),
                offs.get(0).getVelocity(),
                ons.get(0).getChannel(),
                ons.get(0).getMarkTicks(),
                offs.get(0).getMarkTicks());
    }

    public long getTicks() {
        return offTicks - onTicks;
    }

    @Override
    public String toString() {
        return "StanderChord{" +
                "codes=" + codes +
                ", onVelocities=" + onVelocities +
                ", offVelocities=" + offVelocities +
                ", onChannels=" + onChannels +
                ", offChannels=" + offChannels +
                ", onTicks=" + onTicks +
                ", offTicks=" + offTicks +
                '}';
    }
}
