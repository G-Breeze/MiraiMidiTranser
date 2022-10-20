package org.miditranser.data;

import org.miditranser.data.midi.message.NoteMessage;

import java.util.List;

public class CommonChord extends AbstractChord {
    public CommonChord(List<? extends NoteMessage> messages) {
        super(messages);
    }
}
