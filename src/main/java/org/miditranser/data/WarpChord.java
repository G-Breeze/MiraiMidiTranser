package org.miditranser.data;

import org.miditranser.Utils;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.NoteOffMessage;
import org.miditranser.data.midi.message.NoteOnMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WarpChord extends AbstractChord {

    public List<NoteOnMessage> getOnMessages() {
        return getSortedNoteMessages()
                .subList(0, getMessageCount() / 2)
                .stream().map(i-> ((NoteOnMessage) i)).collect(Collectors.toList());
    }

    public List<NoteOffMessage> getOffMessages() {
        return getSortedNoteMessages()
                .subList(getMessageCount() / 2, getMessageCount())
                .stream().map(i-> ((NoteOffMessage) i)).collect(Collectors.toList());
    }

    protected WarpChord(List<? extends NoteMessage> noteMessages) {
        super(noteMessages);
        var sortedMessages = getSortedNoteMessagesStream()
                .map(i-> ((NoteMessage) i)).collect(Collectors.toList());

        var part1 = getOnMessages();
        var part2 = getOffMessages();

        var sameHeads = Utils.getSameHeadElement(part1, HasMidiTicks::getMarkTicks);
        var sameTails = Utils
                .getSameHeadElement(Utils.let(part2, e -> {
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

        assert beCutHead != null;

        part1.remove(beCutHead);
        part2.remove(beCutTail);

        NotePiece head = new NotePiece(beCutHead);

        var cut = new ArrayList<NoteMessage>();

        cut.addAll(part1);
        cut.addAll(part2);

        NotePiece tail = new NotePiece(beCutTail);
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        return null;
    }
}
