package org.miditranser.data;

import org.miditranser.EventStateMachine;
import org.miditranser.MiderTrackWriter;
import org.miditranser.Utils;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.NoteOffMessage;
import org.miditranser.data.midi.message.NoteOnMessage;
import org.miditranser.handle.MessageHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommonChord extends AbstractChord {
    public CommonChord(List<? extends NoteMessage> messages) {
        super(messages);
    }

    public void parse() {
        var sorted = noteMessages.stream().sorted(
                        Comparator
                                .comparingLong(i -> ((HasMidiTicks) i).getMarkTicks())
                                .thenComparingInt(i -> ((NoteMessage) i).getCode())
                )
                .map(i -> ((NoteMessage) i))
                .collect(Collectors.toList());
        var part1 = sorted.subList(0, sorted.size() / 2);
        var part2 = Utils.let(sorted.subList(sorted.size() / 2, sorted.size()), i -> {
            var reversed = new ArrayList<>(i);
            Collections.reverse(reversed);
            return reversed;
        });

        // if part 1 contains off or part2 contains on, then head code of p1 and p2 must be equals
        // else part1 is all on and part2 is all off then assume it is a warp chord

        var sameHeads = Utils.getSameHeadElement(part1, HasMidiTicks::getMarkTicks);
        var sameTails = Utils.getSameHeadElement(part2, HasMidiTicks::getMarkTicks);

        System.out.println("sorted: " + sorted);
        System.out.println("part1: " + part1);
        System.out.println("part2: " + part2);
        System.out.println("head: " + sameHeads);
        System.out.println("tail: " + sameTails);

    }

    private final List<Addable> addables = new ArrayList<>();

    public String parse2(int division) {
        var sorted = noteMessages.stream().sorted(
                        Comparator
                                .comparingLong(i -> ((HasMidiTicks) i).getOrder())
                                .thenComparingInt(i -> ((NoteMessage) i).getCode())
                )
                .map(i -> ((NoteMessage) i))
                .collect(Collectors.toList());
        var part1 = sorted.subList(0, sorted.size() / 2);
        var part2 = new ArrayList<>(sorted.subList(sorted.size() / 2, sorted.size()));

        var sameHeads = Utils.getSameHeadElement(part1, HasMidiTicks::getMarkTicks);
        var sameTails = Utils.getSameHeadElement(Utils.let(part2, e -> {
            var reversed = new ArrayList<>(e);
            Collections.reverse(reversed);
            return reversed;
        }), HasMidiTicks::getMarkTicks);

        NoteMessage beCutHead = null;
        NoteMessage beCutTail = null;

        for (var itemInSameHeads : sameHeads) {
            for (var itemInSameTails : sameTails) {
                if (itemInSameHeads.codeEquals(itemInSameTails)) {
                    beCutHead = itemInSameHeads;
                    beCutTail = itemInSameTails;
                }
            }
        }

        System.out.println("sorted: " + sorted);
        System.out.println("sh: " + sameHeads);
        System.out.println("st: " + sameTails);

        if (beCutHead != null) {
            part1.remove(beCutHead);
            part2.remove(beCutTail);

            EventStateMachine esm = new EventStateMachine(division);

            NotePiece head = new NotePiece(beCutHead);
            addables.add(head);
//            esm.getMiderTrackWriter().addNotePiece(head);
//            esm.getMiderTrackWriter().setAddRestAndGap(false);
//        MiderTrackWriter.setAddRestAndGap(false);

            var cut = new ArrayList<NoteMessage>();

            cut.addAll(part1);
            cut.addAll(part2);

            System.out.println("aal: " + cut);

            for (var msg : cut) {
                MessageHandler handler = new MessageHandler(msg);
                esm.stackHandleMessage(handler);
                handler.handle();
            }

            esm.gen();

            NotePiece tail = new NotePiece(beCutTail);
            tail.setLastingTick(tail.getHeadTicks() - cut.get(cut.size() - 1).getMarkTicks());
//            esm.getMiderTrackWriter().addNotePiece(tail);

            List<Addable> items = esm.getMiderTrackWriter().getItems();

            for (var i : items) {
                if (i instanceof CommonChord) {
                    ((CommonChord) i).parse2(division);
                    addables.addAll(((CommonChord) i).addables);
                } else {
                    addables.add(i);
                }
            }

            addables.add(tail);
        } else {
            for (var piece : sorted) {
                if (piece != null) {
                    addables.add(new NotePiece(piece));
                }
            }
        }


         System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + addables);

        return "code";
    }

    @Override
    public String toString() {

        return this.noteMessages.stream().sorted(Comparator.comparingInt(i->i.getOrder())).collect(Collectors.toList()).toString();
    }
}
