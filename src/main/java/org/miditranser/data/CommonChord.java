package org.miditranser.data;

import org.miditranser.EventStateMachine;
import org.miditranser.MiderTrackOptimizer;
import org.miditranser.Utils;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;
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

    public List<Addable> parseMessages(CalculateDurationConfiguration cdc) {

        final List<Addable> addables = new ArrayList<>();

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

//        System.out.println("sorted: " + sorted);
//        System.out.println("sh: " + sameHeads);
//        System.out.println("st: " + sameTails);

        if (beCutHead != null) {
            part1.remove(beCutHead);
            part2.remove(beCutTail);

            EventStateMachine esm = new EventStateMachine(cdc);

            NotePiece head = new NotePiece(beCutHead);
            addables.add(head);
//            esm.getMiderTrackWriter().addNotePiece(head);
//            esm.getMiderTrackWriter().setAddRestAndGap(false);
//        MiderTrackWriter.setAddRestAndGap(false);

            var cut = new ArrayList<NoteMessage>();

            cut.addAll(part1);
            cut.addAll(part2);

//            System.out.println("aal: " + cut);

            for (var msg : cut) {
                MessageHandler handler = new MessageHandler(msg);
                esm.initHandlerStack(handler);
                handler.handle();
            }

            esm.generateList();

            NotePiece tail = new NotePiece(beCutTail);
            tail.setLastingTick(tail.getHeadTicks() - cut.get(cut.size() - 1).getMarkTicks());
//            esm.getMiderTrackWriter().addNotePiece(tail);

            List<Addable> items = esm.getMiderTrackWriter().getItems();

            for (var i : items) {
                if (i instanceof CommonChord) {
                    var la = ((CommonChord) i).parseMessages(cdc);
                    addables.addAll(la);
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

        return addables;
    }

    @Override
    public String toString() {
        return this.noteMessages.stream().sorted(Comparator.comparingInt(i->i.getOrder())).collect(Collectors.toList()).toString();
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        List<Addable> list = parseMessages(cdc);
        StringBuilder builder = new StringBuilder();

        List<Addable> withRestOrGap = MiderTrackOptimizer.addRestOrGapForChord(list, cdc);

        for (var i : withRestOrGap) {
            var code = i.generateMiderCode(cdc);
            builder.append(code);
        }
        return builder.toString();
    }
}
