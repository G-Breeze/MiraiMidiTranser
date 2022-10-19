package org.miditranser.data;

import org.miditranser.Utils;
import org.miditranser.data.midi.message.NoteOffMessage;
import org.miditranser.data.midi.message.NoteOnMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Chord implements FromMidiEvent {

    List<Byte> codes;
    List<Byte> onVelocities;

    List<Byte> offVelocities;

    List<Byte> onChannels;
    List<Byte> offChannels;

    List<Long> onsTicks;
    List<Long> offsTicks;

    public Chord(List<Byte> codes, List<Byte> onVelocities, List<Byte> offVelocities, List<Byte> onChannels, List<Byte> offChannels, List<Long> onsTicks, List<Long> offsTicks) {
        this.codes = codes;
        this.onVelocities = onVelocities;
        this.offVelocities = offVelocities;
        this.onChannels = onChannels;
        this.offChannels = offChannels;
        this.onsTicks = onsTicks;
        this.offsTicks = offsTicks;
    }

    public Chord(List<NoteOnMessage> ons, List<NoteOffMessage> offs) {
//        var onStream = ons.stream();
//        var offStream = offs.stream();
//        var codes = onStream.;
//        var onV = onStream.map(NoteMessage::getVelocity).collect(Collectors.toList());
//        var onC = onStream.map(NoteMessage::getChannel).collect(Collectors.toList());
//        var offV = offStream.map(NoteMessage::getVelocity).collect(Collectors.toList());
//        var offC = offStream.map(NoteMessage::getChannel).collect(Collectors.toList());
        this(
                ons.stream().map(NoteOnMessage::getCode).collect(Collectors.toList()),
                ons.stream().map(NoteOnMessage::getVelocity).collect(Collectors.toList()),
                offs.stream().map(NoteOffMessage::getVelocity).collect(Collectors.toList()),
                ons.stream().map(NoteOnMessage::getChannel).collect(Collectors.toList()),
                offs.stream().map(NoteOffMessage::getChannel).collect(Collectors.toList()),
                ons.stream().map(NoteOnMessage::getMarkTicks).collect(Collectors.toList()),
                offs.stream().map(NoteOffMessage::getMarkTicks).collect(Collectors.toList())
        );
    }

    public List<String> getNoteNames() {
        return codes.stream().map(Utils::transferNoteNameForChord).collect(Collectors.toList());
    }

    @Override
    public long getHeadTicks() {
        return onsTicks.get(0);
    }

    @Override
    public long getTailTicks() {
        return offsTicks.get(offsTicks.size() - 1);
    }

    public static <T> List<T> coordinate(T t, int size) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(t);
        }
        return list;
    }
}
