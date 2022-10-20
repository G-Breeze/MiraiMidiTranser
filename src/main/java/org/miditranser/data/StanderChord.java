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
public class StanderChord extends CodePairedChord {

    byte onVelocity;
    byte offVelocity;
    byte channel;
    long onTicks;
    long offTicks;

    public StanderChord(List<? extends NoteMessage> noteMessages) {
        super(noteMessages);

        onTicks = getOnsTicks().get(0);
        offTicks = getOffsTicks().get(0);
        onVelocity = getOnChannels().get(0);
        offVelocity = getOffVelocities().get(0);
        channel = getOffChannels().get(0);
    }

//    public StanderChord(List<Byte> codes, byte onVelocity, byte offVelocity, byte channel, long onTicks, long offTicks) {
//        super();
//
//        super(
//                codes,
//                coordinate(onVelocity, codes.size()),
//                coordinate(offVelocity, codes.size()),
//                coordinate(channel, codes.size()),
//                coordinate(channel, codes.size()),
//                coordinate(onTicks, codes.size()),
//                coordinate(offTicks, codes.size()));
//
//
//
//        this.onVelocity = onVelocity;
//        this.offVelocity = offVelocity;
//        this.channel = channel;
//        this.onTicks = onTicks;
//        this.offTicks = offTicks;
//    }

//    public StanderChord(List<NoteOnMessage> ons, List<NoteOffMessage> offs) {
//        this(
//                ons.stream().map(NoteOnMessage::getCode).collect(Collectors.toList()),
//                ons.get(0).getVelocity(),
//                offs.get(0).getVelocity(),
//                ons.get(0).getChannel(),
//                ons.get(0).getMarkTicks(),
//                offs.get(0).getMarkTicks());
//    }

    public long getTicks() {
        return offTicks - onTicks;
    }
}
