package org.miditranser.data;

import org.miditranser.Utils;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.NoteOffMessage;
import org.miditranser.data.midi.message.NoteOnMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractChord implements Chord {

//    List<Byte> codes;
//    List<Byte> onVelocities;
//    List<Byte> offVelocities;
//    List<Byte> onChannels;
//    List<Byte> offChannels;
//    List<Long> onsTicks;
//    List<Long> offsTicks;

    protected List<? extends NoteMessage> noteMessages;

    public List<Byte> getOnCodes() {
        return getOnMessagesStream().map(NoteOnMessage::getCode).collect(Collectors.toList());
    }

    public List<Byte> getOffCodes() {
        return getOffMessagesStream().map(NoteOffMessage::getCode).collect(Collectors.toList());
    }

    public Stream<NoteOnMessage> getOnMessagesStream() {
        return noteMessages.stream().filter(NoteMessage::isNoteOn).map(NoteMessage::asOn);
    }

    public List<NoteOnMessage> getOnMessages() {
        return getOnMessagesStream().collect(Collectors.toList());
    }

    public Stream<NoteOffMessage> getOffMessagesStream() {
        return noteMessages.stream().filter(NoteMessage::isNoteOff).map(NoteMessage::asOff);
    }

    public List<NoteOffMessage> getOffMessages() {
        return getOffMessagesStream().collect(Collectors.toList());
    }

    public List<Byte> getOnVelocities() {
        return getOffMessagesStream().map(NoteOffMessage::getVelocity).collect(Collectors.toList());
    }

    public List<Byte> getOffVelocities() {
        return getOffMessagesStream().map(NoteOffMessage::getVelocity).collect(Collectors.toList());
    }

    public List<Byte> getOnChannels() {
        return getOnMessagesStream().map(NoteOnMessage::getChannel).collect(Collectors.toList());
    }

    public List<Byte> getOffChannels() {
        return getOffMessagesStream().map(NoteOffMessage::getChannel).collect(Collectors.toList());
    }

    public List<Long> getOnsTicks() {
        return getOnMessagesStream().map(NoteOnMessage::getMarkTicks).collect(Collectors.toList());
    }

    public List<Long> getOffsTicks() {
        return getOffMessagesStream().map(NoteOffMessage::getMarkTicks).collect(Collectors.toList());
    }

    public int getMessageCount() {
        return ((int) getNoteMessagesStream().count());
    }

    public Stream<? extends NoteMessage> getNoteMessagesStream() {
        return noteMessages.stream();
    }

    public Stream<? extends NoteMessage> getSortedNoteMessagesStream() {
        return getNoteMessagesStream().sorted(Comparator.comparingLong(HasMidiTicks::getMarkTicks));
    }

    public List<? extends NoteMessage> getSortedNoteMessages() {
        return getNoteMessagesStream()
                .sorted(Comparator.comparingLong(HasMidiTicks::getMarkTicks))
                .collect(Collectors.toList());
    }

    protected AbstractChord(List<? extends NoteMessage> noteMessages) {
        this.noteMessages = noteMessages;
    }


//    @Override
//    public long getHeadTicks() {
//        return onsTicks.get(0);
//    }
//
//    @Override
//    public long getTailTicks() {
//        return offsTicks.get(offsTicks.size() - 1);
//    }

    protected static <T> List<T> coordinate(T t, int size) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(t);
        }
        return list;
    }

    @Override
    public String toString() {
        return "AbstractChord{" +
                "onCodes=" + getOnCodes() +
                ", offCodes=" + getOffCodes() +
                ", onVelocities=" + getOnVelocities() +
                ", offVelocities=" + getOffVelocities() +
                ", onChannels=" + getOnChannels() +
                ", offChannels=" + getOffChannels() +
                ", onTicks=" + getOnsTicks() +
                ", offTicks=" + getOffsTicks() +
                '}';
    }

    @Override
    public long getHeadTicks() {
        return getSortedNoteMessages().get(0).getMarkTicks();
    }

    @Override
    public long getTailTicks() {
        return getSortedNoteMessages().get(getMessageCount() - 1).getMarkTicks();
    }
}