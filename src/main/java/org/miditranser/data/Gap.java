package org.miditranser.data;

import org.miditranser.Utils;

import java.util.Objects;

public class Gap implements Addable {
    public long getTicks() {
        return ticks;
    }

    long ticks;

    public Gap(String symbols) {
        this.symbols = symbols;
    }

    String symbols;

    public Gap(long ticks) {
        this.ticks = ticks;
    }

    @Override
    public String toString() {
        String v = "none";
        if (symbols != null) v = symbols;
        else if (ticks != 0) v = String.valueOf(ticks);

        return "{mark gap=" + v + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gap gap = (Gap) o;

        if (ticks != gap.ticks) return false;
        return Objects.equals(symbols, gap.symbols);
    }

    @Override
    public int hashCode() {
        int result = (int) (ticks ^ (ticks >>> 32));
        result = 31 * result + (symbols != null ? symbols.hashCode() : 0);
        return result;
    }

    public static Gap ClearGap() {
        return new Gap("default");
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        return toString();
    }
}
