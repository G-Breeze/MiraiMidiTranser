package org.miditranser;

import org.miditranser.data.Addable;
import org.miditranser.data.FromMidiEvent;
import org.miditranser.data.HasNoteCode;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiderTrackOptimizer {
    public static int getCommonOctave(List<Addable> list) {
        var map = new HashMap<Integer, Integer>();
        for (var e : list) {
            if (e instanceof HasNoteCode) {
                map.merge(((HasNoteCode) e).getNoteOctave(), 1, Integer::sum);
            }
        }

        return getMostKeyByValue(map, "failed to get common octave.");
    }

    private static void test() {

    }

    public static int getMostKeyByValue(Map<Integer, Integer> map, String failedMsg) {
        var max = new ArrayList<>(map.entrySet())
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));
        if (max.isEmpty())
            throw new RuntimeException(failedMsg);

        return max.get().getKey();
    }


}
