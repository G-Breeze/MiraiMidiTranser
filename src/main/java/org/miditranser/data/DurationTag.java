package org.miditranser.data;

import java.util.Arrays;

import static org.miditranser.Constance.*;

public class DurationTag {

    private final int[] tags = new int[10 + 7];

//    private int plusCount = tags[0];
//    private int minusCount = tags[1];
//    private int dotCount = tags[2];
//    private int div3Count = tags[3];
//    private int div4Count = tags[4];
//    private int div5Count = tags[5];
//    private int div6Count = tags[6];
//    private int div7Count = tags[7];
//    private int div8Count = tags[8];
//    private int div9Count = tags[9];
//    private int mul3Count = tags[10];
//    private int mul4Count = tags[11];
//    private int mul5Count = tags[12];
//    private int mul6Count = tags[13];
//    private int mul7Count = tags[14];
//    private int mul8Count = tags[15];
//    private int mul9Count = tags[16];

    public DurationTag() {
        this(0, 0, 0, 0);
    }

    public DurationTag(int plus, int minus, int dot, int div3) {
        setTags(plus, minus, dot, div3);
    }

    public int[] getTags() {
        return tags;
    }

    public void setTags(int plus, int minus, int dot, int div3) {
        setPlusCount(plus);
        setMinusCount(minus);
        setDotCount(dot);
        setDiv3Count(div3);
    }

    public int getPlusCount() {
        return tags[0];
    }

    public void setPlusCount(int plusCount) {
        tags[0] = plusCount;
    }

    public int getMinusCount() {
        return tags[1];
    }

    public void setMinusCount(int minusCount) {
        tags[1] = minusCount;
    }

    public int getDotCount() {
        return tags[2];
    }

    public void setDotCount(int dotCount) {
        tags[2] = dotCount;
    }

    public int getDiv3Count() {
        return tags[3];
    }

    public void setDiv3Count(int div3Count) {
        tags[3] = div3Count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DurationTag that = (DurationTag) o;

        return Arrays.equals(tags, that.tags);
    }

    public double getValue() {
        return Math.exp(
                (getPlusCount() - getMinusCount()) * ln2 +
                getDotCount() * ln1_5
                + (tags[10] - tags[3]) * ln3
                + (tags[11] - tags[4]) * ln4
                + (tags[12] - tags[5]) * ln5
                + (tags[13] - tags[6]) * ln6
                + (tags[14] - tags[7]) * ln7
                + (tags[15] - tags[8]) * ln8
                + (tags[16] - tags[9]) * ln9
        );
    }

    public String asMiderDurationSymbols() {
        return  "/3".repeat(tags[3]) +
                "/4".repeat(tags[4]) +
                "/5".repeat(tags[5]) +
                "/6".repeat(tags[6]) +
                "/7".repeat(tags[7]) +
                "/8".repeat(tags[8]) +
                "/9".repeat(tags[9]) +
                "x3".repeat(tags[10]) +
                "x4".repeat(tags[11]) +
                "x5".repeat(tags[12]) +
                "x6".repeat(tags[13]) +
                "x7".repeat(tags[14]) +
                "x8".repeat(tags[15]) +
                "x9".repeat(tags[16]) +

                "+".repeat(tags[0]) +
                "-".repeat(tags[1]) +
                ".".repeat(tags[2]);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tags);
    }

    @Override
    public String toString() {
        return "DurationTag{" +
                "tags=" + Arrays.toString(tags) +
                '}';
    }
}
