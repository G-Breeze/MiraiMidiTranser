package org.miditranser;

import org.miditranser.data.*;

import java.util.*;

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

    public static List<Addable> addRestOrGapForChord(List<Addable> items, CalculateDurationConfiguration cdc) {
//        long previousGap = 0;
        FromMidiEvent previous = null;
        var returnList = new ArrayList<Addable>();

        for (var add : items) {

            if (!(add instanceof FromMidiEvent)) {
                returnList.add(add);
                // assume that none FromMidiEvent type are not occupied ticks
                continue;
            }

            var current = (FromMidiEvent) add;
            if (previous != null) {
                long gap = current.getHeadTicks() - previous.getTailTicks();

                if (gap != 0) {
                    var rest = new Rest(previous.getTailTicks(), current.getHeadTicks());
                    returnList.add(rest);
                }

                returnList.add(current);
            } else {
                returnList.add(add);
            }

            previous = current;
        }

        return returnList;
    }

    public static List<Addable> withRestOrGap(List<Addable> items, CalculateDurationConfiguration cdc) {

        FromMidiEvent previous = null;
        long previousGap = 0;
        Stack<Gap> gapStack = new Stack<>();
        var retList = new ArrayList<Addable>();
        // options
        var useGap = cdc.isRestUseGap();
        var onlyClearOnce = cdc.isRestOnlyClearGapOnce();
        var gapUseDurationSymbolsForm = cdc.isRestGapUseDurationSymbolsForm();
        var autoClose = cdc.isRestAutoCloseGap();

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
                    if (useGap && rest.shouldUseGap(cdc)) {

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
//                 if (current instanceof NotePiece) {
//                     // NotePiece now is not support lasting ticks so should be ignored
//                     // todo support lasting ticks
//                     retList.add(current);
//                     continue;
//                 }

                var rest = new Rest(0, current.getHeadTicks());
                retList.add(rest);
            }

            previous = current;
            retList.add(item);
        }

        if (autoClose && !gapStack.isEmpty()) retList.add(Gap.ClearGap());
        return retList;
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
