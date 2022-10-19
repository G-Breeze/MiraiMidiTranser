package org.miditranser;

import kotlin.Triple;
import org.miditranser.data.*;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.handle.MessageHandler;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.miditranser.Utils.calculateDuration;
import static org.miditranser.Utils.transferNoteName;

public class MiderTrackWriter {

    private final int division;
    public static final int defaultBpm = 80;

    public boolean isAddRestOrGap() {
        return addRestOrGap;
    }

    public void setAddRestOrGap(boolean addRestOrGap) {
        this.addRestOrGap = addRestOrGap;
    }

    private boolean addRestOrGap = true;

    MiderTrackWriter(int division) {
        this.division = division;
    }

    int bpm;

    /**
     * first: code
     * second: duration
     * third: channel
     */
    private final List<Triple<Byte, DurationTag, Byte>> noteList = new ArrayList<>();

    private final List<Addable> items = new ArrayList<>();

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void addCombinedNote(NoteMessage on, NoteMessage off) {

        CheckEventResult result = checkNote(on, off);
        if (!result.isNoteClosed())
            throw new RuntimeException("not closed pair");

        boolean isStander = result.isNoteCodeSameFlag() && result.isNoteChannelSameFlag();

        if (isStander)
            items.add(new Note(on.asOn(), off.asOff()));
        else {
            items.add(new NotePiece(on));
            NotePiece offPiece = new NotePiece(off);
            offPiece.setLastingTick(off.getMarkTicks() - on.getMarkTicks());
            items.add(offPiece);
        }
    }

    public static CheckEventResult checkChord(List<NoteMessage> ons, List<NoteMessage> offs) {

        CheckEventResult result = new CheckEventResult();
        long startTicks = ons.get(0).getMarkTicks();
        int startChannel = ons.get(0).getChannel();
        long endTicks = offs.get(0).getMarkTicks();
        int endChannel = offs.get(0).getChannel();

        if (ons.size() != offs.size()) result.setChordSizeSameFlag(false);

        ons.forEach(e -> {
            if (!e.isNoteOn()) result.setChordAllOnFlag(false);
            if (e.getMarkTicks() != startTicks) result.setChordStartTicksSameFlag(false);
            if (e.getChannel() != startChannel) result.setChordOnChannelsSameFlag(false);
        });

        offs.forEach(e -> {
            if (!e.isNoteOff()) result.setChordAllOffFlag(false);
            if (e.getMarkTicks() != endTicks) result.setChordEndTicksSameFlag(false);
            if (e.getChannel() != endChannel) result.setChordOffChannelsSameFlag(false);
        });

        return result;
    }

    public static boolean isStanderChord(CheckEventResult r) {
        if (!r.isChordSizeSameFlag())
            return false;
        if (!r.isChordAllOnFlag() || !r.isChordAllOffFlag())
            return false;
        return r.isChordStartTicksSameFlag() && r.isChordEndTicksSameFlag() && r.isChordOnChannelsSameFlag() && r.isChordOffChannelsSameFlag();
    }

    public static CheckEventResult checkNote(NoteMessage on, NoteMessage off) {
        CheckEventResult result = new CheckEventResult();

        if (!on.isNoteOn() || !off.isNoteOff())
            result.setNoteClosed(false);

        if (on.getCode() != off.getCode())
            result.setNoteCodeSameFlag(false);

        if (on.getChannel() != off.getChannel())
            result.setNoteChannelSameFlag(false);

        return result;
    }

    public void addCombinedChord(List<NoteMessage> ons, List<NoteMessage> offs) {

        CheckEventResult result = checkChord(ons, offs);

//        if (!result.isChordSizeSameFlag())
//            throw new RuntimeException();
//        if (!result.isChordAllOnFlag()|| !result.isChordAllOffFlag())
//            throw new RuntimeException();

//        var entries = IntStream.range(0, ons.size())
//                .mapToObj(i -> Map.entry(ons.get(i).asOn(), offs.get(i).asOff()))
//                .collect(Collectors.toList()); // todo useful

//        boolean isStanderChord = result.isChordStartTicksSameFlag() && result.isChordEndTicksSameFlag();

        if (isStanderChord(result)) {
            // stander chord
            StanderChord chord = new StanderChord(
                    ons.stream().map(NoteMessage::asOn).collect(Collectors.toList()),
                    offs.stream().map(NoteMessage::asOff).collect(Collectors.toList()));
            items.add(chord);

        } else {
            /// System.out.println("none stander");
        }
    }

    public void addChord(List<NoteMessage> messages) {

        List<NoteMessage> onMessages = messages.stream().filter(NoteMessage::isNoteOn).collect(Collectors.toList());
        List<NoteMessage> offMessages = messages.stream().filter(NoteMessage::isNoteOff).collect(Collectors.toList());

        CheckEventResult result = checkChord(onMessages, offMessages);

        if (!result.isChordSizeSameFlag())
            throw new RuntimeException("size not same!");

        if (messages.size() < 4)
            throw new RuntimeException("size not same!");

        if (isStanderChord(result)) {
//            System.out.println("stander chord");
            items.add(new StanderChord(
                    onMessages.stream()
                            .map(NoteMessage::asOn)
                            .collect(Collectors.toList()),
                    offMessages.stream()
                            .map(NoteMessage::asOff)
                            .collect(Collectors.toList()))
            );
        } else if (isArpeggio(result)) {
//            System.out.println("Arpeggio chord");
        } // else if (result.isChordStartTicksSameFlag()) {
            // start ticks are same
            // var f = (List<NoteMessage>) sortedByMarkTicks(messages);

        // } else if (result.isChordEndTicksSameFlag()) {
            // end ticks are same
        /*}*/ else {
//            System.out.print("inner chord: " + messages);
//            System.out.println();
//
//            var collect = messages.stream()
//                    .sorted(Comparator.comparingLong(HasMidiTicks::getMarkTicks))
//                    .map(NoteMessage::asStackString);
//
////            long count = collect.count();
//            List<String> list = collect.collect(Collectors.toList());
            var sortedMessages = sortedByMarkTicks(messages)
                    .map(i -> (NoteMessage) i )
                    .collect(Collectors.toList());

//            System.out.println(sortedMessages);

            var part1 = sortedMessages.subList(0, sortedMessages.size() / 2);
            var part2 = sortedMessages.subList(sortedMessages.size() / 2, sortedMessages.size());

            var eqp1 = getSameHeadElement(part1);
            var copyPart2 = new ArrayList<>(part2);
            Collections.reverse(copyPart2);
            var eqp2 = getSameHeadElement(copyPart2);

            NoteMessage headMessage = null;
            NoteMessage tailMessage = null;

//            System.out.println("p1: " + eqp1);
//            System.out.println("p2: " + eqp2);

            for (var head : eqp1) {
                for (var tail : eqp2) {
                    if (head.codeEquals(tail)) {
                        headMessage = head;
                        tailMessage = tail;
                         System.out.println("      :head>" + head + ", tail>" + tail);
                    } else break;
                }
            }

//            assert headMessage != null;

            if (headMessage != null) {
                // Warp chord
                sortedMessages.remove(headMessage);
                sortedMessages.remove(tailMessage);

                var headNp = new NotePiece(headMessage);
                var tailNp = new NotePiece(tailMessage);

                tailNp.setLastingTick(tailNp.getTailTicks() - sortedMessages.get(sortedMessages.size() - 1).getMarkTicks());

                items.add(headNp);

                EventStateMachine handel = new EventStateMachine(division);
                for (NoteMessage message : sortedMessages) {
                    MessageHandler mh = new MessageHandler(message);
                    handel.stackHandleMessage(mh);
                    mh.handle();
                }

                handel.gen();
                items.addAll(handel.getMiderTrackWriter().getOriginalList());

                items.add(tailNp);

            } else {
                // change directly to note piece
                var last = sortedMessages.get(0).getMarkTicks();
                for (var msg : sortedMessages) {
                    NotePiece piece = new NotePiece(msg);
                    if (piece.isNoteOff()) {
                        piece.setLastingTick(msg.getMarkTicks() - last);
                    }
                    last = msg.getMarkTicks();
                }
            }



//            System.out.println("now: " + sortedMessages);
//            System.out.println();

            // System.out.println("inner>>>>" + collect.collect(Collectors.joining(" ")));

//            System.out.println(onMessages.size());
//            System.out.println(offMessages.size());


//            var sorted = messages.stream()
//                    .sorted(Comparator.comparingLong(HasMidiTicks::getMarkTicks))
//                    .collect(Collectors.toList());
////            System.out.println(sorted);
//
//            var cutMessages = new ArrayList<>(sorted);
//            var head = cutMessages.remove(0);
//            var tail = cutMessages.remove(cutMessages.size() - 1);
//            NotePiece onPiece = new NotePiece(head);
//            NotePiece offPiece = new NotePiece(tail);
//            offPiece.setLastingTick(tail.getMarkTicks() - head.getMarkTicks());
//
//            items.add(onPiece);
//
//            EventStateMachine handel = new EventStateMachine(division);
//            for (NoteMessage message : sortedMessages) {
////            for (NoteMessage message : cutMessages) {
//                MessageHandler mh = new MessageHandler(message);
//                handel.stackHandleMessage(mh);
//                mh.handle();
//            }
//
//            handel.gen();
//            items.addAll(handel.getMiderTrackWriter().getOriginalList());
//
//            items.add(offPiece);
        }

//        Chord chord = new Chord(ons.stream().map(NoteMessage::asOn).collect(Collectors.toList()),
//                offs.stream().map(NoteMessage::asOff).collect(Collectors.toList()));
//        items.add(chord);
    }

    public static List<NoteMessage> getSameHeadElement(List<? extends NoteMessage> list) {
        ArrayList<NoteMessage> retElement = new ArrayList<>();
        long ticks = list.get(0).getMarkTicks();
        for(var i : list) {
            if (i.getMarkTicks() == ticks) {
                retElement.add(i);
            } else break;
        }
        return retElement;
    }

    public static Stream<? extends HasMidiTicks> sortedByMarkTicks(List<? extends HasMidiTicks> provide) {
        return provide.stream()
                .sorted(Comparator.comparingLong(HasMidiTicks::getMarkTicks));
    }

    private Collection<? extends Addable> getOriginalList() {
        return items;
    }

    private boolean isArpeggio(CheckEventResult result) {
        return false;
    }

//    public void op() {
//        var vm = MiderTrackOptimizer.getCommonOctave(items);
//        System.out.println("common octave: " + vm);
//    }

    public List<Addable> removeRestAndGapInsideNotePiece(List<Addable> items) {

        var list = new ArrayList<>(items);
        var stack = new Stack<MidiHexData>();

        for (var e : list) {
            if (e instanceof MidiHexData) {
                if (((MidiHexData) e).stackTypeIsPush()) {
                    stack.push(((MidiHexData) e));
                } else {
                    stack.pop();
                }
            }

            if (!stack.isEmpty()) {
                if (e instanceof Rest || e instanceof Gap) {
                    items.remove(e);
                } else {
                    System.out.println(":::::::::::::::::" + e);
                }
            }

        }

        return list;
    }

    public List<Addable> withRestOrGap(List<Addable> items) {

//        System.out.println("wro" + items);

        FromMidiEvent previous = null;
        long previousGap = 0;
        Stack<Gap> gapStack = new Stack<>();
        var retList = new ArrayList<Addable>();
        // options
        var useGap = !true;
        var onlyClearOnce = true;
        var gapUseDurationSymbolsForm = false;
        var autoClose = false;


        for (Addable item : items) {

            var current = (FromMidiEvent) item;

            if (previous != null) {
                // case index >= 1
                var gap = current.getHeadTicks() - previous.getTailTicks();
                if (gap != 0) {
                    // contains gap that need to be fulfilled with rest or gap pair
                    var rest = new Rest(previous.getTailTicks(), current.getHeadTicks());
                    if (useGap && rest.shouldUseGap(division)) {

                        if (gap != previousGap) {
                            // gap value changed
                            var gapPair = rest.toGapPair(gapUseDurationSymbolsForm);
                            gapStack.push(gapPair.getValue());
                            retList.add(gapPair.getKey());
                        }
                    } else {
                        if (autoClose) while (!gapStack.isEmpty()) {
                            retList.add(gapStack.pop());
                            if (onlyClearOnce) gapStack.clear();
                        }
                        retList.add(rest);
                    }
                } else {
                    // clear gap
                    if (autoClose) while (useGap && !gapStack.isEmpty()) {
                        retList.add(gapStack.pop());
                        if (onlyClearOnce) gapStack.clear();
                    }
                }
                previousGap = gap;
            } else if (current.getHeadTicks() != 0) {
                // case index = 0
                var rest = new Rest(0, current.getHeadTicks());
                retList.add(rest);
            }

            previous = current;
            retList.add(item);
        }

        if (autoClose && !gapStack.isEmpty()) retList.add(Gap.ClearGap());
        return retList;
    }

    public List<Addable> getOptimizedList() {
//        var nl = ;
         return withRestOrGap(items);//removeRestAndGapInsideNotePiece();
//        return nl;
    }

    public void addRest() {
        FromMidiEvent previous = null;
        for (int i = 0; i < items.size(); i++) {
            var current = (FromMidiEvent) items.get(i);
            if (previous != null) {
                var gap = current.getHeadTicks() - previous.getTailTicks();
                if (gap != 0) {

                    var symbols = calculateDuration(gap, division).asMiderDurationSymbols();
                    if (symbols.length() > 4) {

                    } else {
//                        Rest
                        System.out.println("add rest o"+symbols);
                    }
                    System.out.println("gap: " + gap + ", index: " + i);
                }
            }
            previous = current;
        }
    }

    public String getTrackCode() {
        StringBuilder builder = new StringBuilder();

        // todo remove to FromMidiEvents

        var accuracy = 0.1;

        if (bpm == defaultBpm || bpm == 0)
            builder.append(">g>");
        else builder.append(">").append(bpm).append("b>");

        var list = getOptimizedList();
//        removeRestAndGapInsideNotePiece(items);

        for (var item : list) {
            if (item instanceof Note) {
                String name = ((Note) item).getNoteName();
                long ticks = ((Note) item).getTicks();
                builder.append(name).append(calculateDuration(ticks, division, accuracy).asMiderDurationSymbols());
            } else if (item instanceof StanderChord) {

                var names = ((StanderChord) item).getNoteNames();
                var symbols = calculateDuration(((StanderChord) item).getTicks(), division, accuracy).asMiderDurationSymbols();
                var root = names.get(0);
                var restNotes = names.subList(1, names.size());
                var suffixPart = String.join(":", restNotes);

                builder.append(root).append(symbols).append(":").append(suffixPart);
            } else if (item instanceof Rest) {
                var symbols = calculateDuration(((Rest) item).getTicks(), division, accuracy).asMiderDurationSymbols();
                builder.append("o").append(symbols);
            } else if (item instanceof Gap) {
                builder.append(item);
            } else if (item instanceof NotePiece) {
                builder.append("{");
                if (((NotePiece) item).isNoteOn())
                    builder.append("on");
                else if (((NotePiece) item).isNoteOff())
                    builder.append("off");
                builder.append(((NotePiece) item).getNoteName());
                if (((NotePiece) item).isNoteOff())
//                    builder.append(calculateDuration(((NotePiece) item).getLastingTick(), division, accuracy).asMiderDurationSymbols());
//                    builder.append(((NotePiece) item).getLastingTick());
                    builder.append(",0");
                builder.append("}");
            }
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void addSingleNote(long time, byte code, byte channel) {
        noteList.add(new Triple<>(code, calculateDuration(time, division), channel));
    }
}
