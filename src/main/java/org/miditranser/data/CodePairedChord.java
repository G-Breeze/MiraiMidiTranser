package org.miditranser.data;

import org.miditranser.Utils;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.NoteOnMessage;

import java.util.List;
import java.util.stream.Collectors;

abstract class CodePairedChord extends AbstractChord {

    CodePairedChord(List<? extends NoteMessage> noteMessages) {
        super(noteMessages);
    }

    public List<Byte> getCodes() {
        return getOnMessagesStream().map(NoteOnMessage::getCode).collect(Collectors.toList());
    }

    public List<String> getNoteNames() {
        return getCodes().stream().map(Utils::transferNoteNameForChord).collect(Collectors.toList());
    }
}
