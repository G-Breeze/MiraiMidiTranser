package org.miditranser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.miditranser.data.duration.DurationTag;

class UtilsTest {

    @Test
    void transferNoteName() {
        var name = Utils.transferNoteName((byte) 62);
        Assertions.assertEquals("d", name);
    }

    @Test
    void calculateSpecialDurationPro() {
        int ticks = 24;
        int division = 960;

        DurationTag tag = Utils.calculateSpecialDurationPro(ticks, division, 0.5);
        System.out.println(tag);
        System.out.println(tag.getValue() * division);
        Assertions.assertTrue(Math.abs(tag.getValue() * division - ticks) < 3);
    }

    @Test
    void calculateSpecialDuration() {
        int ticks = 114514;
        int division = 960;

        DurationTag tag = Utils.calculateSpecialDuration(ticks, division, 0.0000001);
        Assertions.assertTrue(Math.abs(tag.getValue() * division - ticks) < 0.1);
    }
}