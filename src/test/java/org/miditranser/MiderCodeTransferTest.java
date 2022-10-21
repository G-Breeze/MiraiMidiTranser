package org.miditranser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MiderCodeTransferTest {

    @Test
    void parseMidiSource() throws InvalidMidiDataException, IOException {
        String path = Objects.requireNonNull(Main.class.getResource("/midis/nexttoyou.mid")).getPath();

        var seq = MidiSystem.getSequence(new File(path));
        String parse = MiderCodeTransfer.parseMidiSource(seq);
        var expected = ">g> G- E- G- B- A. G-- #F-- G. A-- G-- #F- E- D- #F- G- E- G- B- A. G-- #F-- G. A-- G-- A- C6- B- #F- G- E- G- D6- A. G-- #F-- #F. A-- G-- D6 B- A- G- b- E- B- A. G-- #F-- #F. A-- G-- #F- C6- B- #F- G- E- G- B- A. G-- #F-- G. A-- G-- #F- E- D- #F- G- E- G- B- A. G-- #F-- #F. A-- G-- #F- C6- B- #F- G- E- G- D6- A. G-- #F-- #F. A-- G-- D6 B- A- G- b- E- B- A. G-- #F-- #F. A-- G-- #F- C6- B- #F- G+ G+ G+ #F+ E+ E+ G+ #F G A+ A+ B+ C6+ B+ B+ A+ A G G+ G+ G+ A+ E+ C6+ B+ A G B+ A+ D6+ C6 B B C6- B- A- B- A- G- E E- C6- B G- A- A. #F- G- A- B- C6- B. G- #F G- A- A. A- A- G- #F- G- E. E- C6- B- A- G- A. #F- #F- G- A- B- B. G- #F G- A- B+ A+ C6+ B+";
        Assertions.assertEquals(expected, parse.trim());
    }
}