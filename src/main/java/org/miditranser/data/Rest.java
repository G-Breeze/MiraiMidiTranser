package org.miditranser.data;

import org.miditranser.Utils;

import java.util.Map;

import static org.miditranser.Utils.calculateDuration;

public class Rest implements Addable {
    long startTicks;
    long endTicks;

    public Rest(long startTicks, long endTicks) {
        this.startTicks = startTicks;
        this.endTicks = endTicks;
    }

    public Gap toGap() {
        return new Gap(getTicks());
    }

    public Map.Entry<Gap, Gap> toGapPair(boolean gapUseDurationSymbolsForm) {
        return Map.entry(new Gap(getTicks()), Gap.ClearGap());
    }

    public boolean shouldUseGap(int division) {
        return getTicks() <= 48 || getDurationSymbols(division).length() > 5;
    }

    public String getDurationSymbols(int division) {
        return Utils.calculateDuration(getTicks(), division).asMiderDurationSymbols();
    }

    @Override
    public String toString() {
        return "rest ticks: " + (endTicks - startTicks);
    }

    public long getTicks() {
        return endTicks - startTicks;
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        var symbols = calculateDuration(getTicks(), cdc.division, cdc.accuracy).asMiderDurationSymbols();
        return "o" + symbols;
    }
}
