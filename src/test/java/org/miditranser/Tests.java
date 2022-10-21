package org.miditranser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.miditranser.data.Addable;
import org.miditranser.data.FromMidiEvent;
import org.miditranser.data.Gap;
import org.miditranser.data.Rest;

import java.util.ArrayList;
import java.util.List;

import static org.miditranser.Utils.*;
import static whiter.music.mider.UtilsKt.*;

public class Tests {

    @Test
    public void utilToHexTest() {
        var hex = "abc120";
        var data = Integer.valueOf(hex, 16);
        var trans = Utils.toHex(as4lByteArray(data));
        Assertions.assertEquals(data, Integer.valueOf(trans, 16));
    }

    @Test
    public void utilRestByteArrayTest() {
        var arr = new byte[] {1, 2, 3};
        Assertions.assertArrayEquals(new byte[] {2, 3}, rest(arr));
    }

    @Test
    public void utilByteArrayToIntTest() {
        int i = 5463161;
        var bytes = as4lByteArray(i);
        Assertions.assertEquals(i, byteArrayToInt(bytes));
    }

    @Test
    public void utilMinTest() {
        var da = new double[]{.1, .3, .01};

        Assertions.assertEquals(.01, min(da[0], da[1], da[2]));
    }

    @Test
    public void utilMinIndexTest() {
        var da = new double[]{.1, .3, .01};
        Assertions.assertEquals(2, minIndex(da));
    }

    @Test
    public void test() {
//        ArrayList<String> list1 = new ArrayList<>();
//        ArrayList<String> list2 = new ArrayList<>();
//
////        var t = IntStream
////                .range(0, Math.min(list1.size(), list2.size()))
////                .mapToObj(i -> new HashMap<String, String>())
////                .collect(Collectors.toList());
//
//        list1.add("1");
//        list1.add("2");
//        list1.add("3");
//        list1.add("4");
//        list1.add("5");
////
//        for (int i = 0; i < list1.size(); i++) {
//            if (list1.get(i).equals("3")) {
//                list1.add(i + 1, "3.5");
//            }
//        }
////
//        System.out.println(list1);

        System.out.println(Gap.ClearGap().equals(new Gap("default")));

        Assertions.assertEquals(1, 1);
    }

    @Test
    public void testWithRestOrGap() {
        List<Addable> addables = new ArrayList<>(); //todo add withRestOrGap();
        long ticks = 0;
        long gap = 0;
        for (var a : addables) {
            if (a instanceof FromMidiEvent) {
                long occupy = ((FromMidiEvent) a).getTailTicks() - ((FromMidiEvent) a).getHeadTicks();
                System.out.println("occupy: " + occupy);
                ticks += occupy + gap;
            } else if (a instanceof Rest) {
                ticks += ((Rest) a).getTicks();
            } else if (a instanceof Gap) {
                if (!a.equals(Gap.ClearGap())) {
                    gap = ((Gap) a).getTicks();
                } else gap = 0;
            } else {
                System.out.println("????????????????????");
            }
        }

        System.out.println("ticks " + ticks);
    }

}
