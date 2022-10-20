package org.miditranser;

import org.miditranser.data.*;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.ProgramChangeMessage;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class MiderTrackWriter {

    private final int division;
    public static final int defaultBpm = 80;
    private boolean addRestAndGap = true;
    int bpm;
    private final List<Addable> items = new ArrayList<>();

    MiderTrackWriter(int division) {
        this.division = division;
    }

    public boolean isAddRestAndGap() {
        return addRestAndGap;
    }

    public void setAddRestAndGap(boolean addRestAndGap) {
        this.addRestAndGap = addRestAndGap;
    }

    public List<Addable> getItems() {
        return items;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    private void addStanderNote(NoteMessage on, NoteMessage off) {

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

    public void addSound(List<NoteMessage> messages) {
        if (messages.size() == 2) {
            // might note
            addStanderNote(messages.get(0), messages.get(1));
        } else if (messages.size() >= 4) {
            // might chord
            addChord(messages);
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

        if (isStanderChord(result))
            items.add(new StanderChord(messages));
        else if (isArpeggio(result)) {
            items.add(new CommonChord(messages));
        } else items.add(new CommonChord(messages));
    }

    private boolean isArpeggio(CheckEventResult result) {
        return false;
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

            if (!(item instanceof FromMidiEvent)) {
                retList.add(item);
                continue;
                // todo optimize
            }

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
        if (isAddRestAndGap())
            return withRestOrGap(items);
        else return items;
    }

    public String generateTrackMiderCode() {
        var cdc = new CalculateDurationConfiguration();
        StringBuilder builder = new StringBuilder();
        cdc.setAccuracy(0.1);
        cdc.setDivision(division);

        for (var item : getOptimizedList()) {
            String code = item.generateMiderCode(cdc);
            builder.append(code).append(" ");
        }

        return builder.toString();
    }

/*    public String getTrackCode() {
        StringBuilder builder = new StringBuilder();

        // todo remove to FromMidiEvents

        var accuracy = 0.1;

        var list = getOptimizedList();

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
            } else if (item instanceof CommonChord) {

                builder.append("<");
                builder.append(((CommonChord) item).parseMessages(division));
                builder.append(">");
//                System.out.println("code > " + ((CommonChord) item).parse2(division));
//                ((CommonChord) item).parse();
            } else if (item instanceof AbstractChord) {
                builder.append(((AbstractChord) item).getOnCodes());
                builder.append("%");
                builder.append(((AbstractChord) item).getOnsTicks());
                builder.append(" & ");
                builder.append(((AbstractChord) item).getOffCodes());
                builder.append("%");
                builder.append(((AbstractChord) item).getOffsTicks());
            } else {
                System.out.println("not in");
            }
            builder.append(" ");
        }

        return builder.toString().trim();
    }*/

    public String generateTrackCodeWithConfig() {

        StringBuilder builder = new StringBuilder();

        if (bpm == defaultBpm || bpm == 0)
            builder.append(">g>");
        else builder.append(">").append(bpm).append("b>");

        return builder.append(generateTrackMiderCode()).toString();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void addNoneSound(Addable addable) {
        items.add(addable);
    }
}
