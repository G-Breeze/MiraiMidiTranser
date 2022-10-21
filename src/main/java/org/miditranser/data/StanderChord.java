package org.miditranser.data;

import org.miditranser.data.midi.message.NoteMessage;

import java.util.List;

import static org.miditranser.Utils.calculateDuration;

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

    @Override
    public String toString() {
        return String.join(":", getNoteNames());
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

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        var names = getNoteNames();
        var symbols = calculateDuration(getTicks(), cdc.division, cdc.accuracy).asMiderDurationSymbols();
        var root = names.get(0);
        var restNotes = names.subList(1, names.size());
        var suffixPart = String.join(":", restNotes);
        return root + symbols + ":" + suffixPart;
    }
}
