package org.miditranser;

import org.miditranser.data.HasNoteCode;
import org.miditranser.data.duration.DurationTag;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.miditranser.Constance.*;

public class Utils {

    /**
     * calculate the duration described in mider by given ticks
     *
     * @param ticks    given ricks
     * @param division ticks of quarter note
     * @return the combination of +-./3 count
     */
    public static DurationTag calculateDuration(long ticks, int division, double accuracy) {

        if (ticks <= 0 || division <= 0) throw new RuntimeException("ticks: " + ticks + " or division: " + division + " can not be zero or negative.");

        var tag = new DurationTag();

        if (ticks == division) {
            // do nothing
        } else if (ticks / division == 2) {
            tag.setPlusCount(1);
        } else if (ticks / division == 4) {
            tag.setPlusCount(2);
        } else if (ticks / division == 8) {
            tag.setPlusCount(3);
        } else if (division / ticks == 2) {
            tag.setMinusCount(1);
        } else if (division / ticks == 4) {
            tag.setMinusCount(2);
        } else if (division / ticks == 8) {
            tag.setMinusCount(3);
        } else if (division / ticks == 3) {
            tag.setDiv3Count(1);; // a/3
        } else if (division / ticks == 6) {
            tag.setMinusCount(1);
            tag.setDiv3Count(1); // a/3-
        } else if (division / (float) ticks == 1.5f) {
            tag.setPlusCount(1);
            tag.setDiv3Count(1); // a/3+
        } else if (ticks / division == 6) {
            tag.setPlusCount(1);
            tag.setDotCount(1); // a+.
        }  else if (ticks / (float) division == 0.75f) {
            tag.setMinusCount(1);
            tag.setDotCount(1); // a-.
        } else if (ticks / (float) division == 1.5f) {
            tag.setDotCount(1); // a.
        } else if (ticks / (float) division == 1.5f * 1.5f) {
            tag.setDotCount(2); // a..
        }  else if (ticks / (float) division == 1.5f * 1.5f * 1.5f) {
            tag.setDotCount(3); // a...
        } else {
            // special duration
            tag = calculateSpecialDuration(ticks, division, accuracy);
        }

        return tag;
    }

    public static DurationTag calculateDuration(long ticks, int division) {
        return calculateDuration(ticks, division, 0.5);
    }

    public static DurationTag calculateSpecialDuration(long ticks, int division) {
        return calculateSpecialDuration(ticks, division, 0.01);
    }

    public static DurationTag calculateSpecialDuration(long ticks, int division, double accuracy) {

        // N, accuracy and quarter is known,
        // let M = 2^a * 0.5^b * 1.5^c * (1/3)^d * quarter
        // require a set of positive integer (a, b, c, d) that satisfies | M - N | < accuracy
        //
        // let r = a - b
        // let t = lnM = aln2 -bln2 + cln1.5 - dln3 + ln quarter = rln2 + cln1.5 - dln3 + ln quarter
        // let s = lnN
        // let u = s - ln quarter

        // fix d = 0

        var approach = .0;
        var lnN = Math.log(ticks);
        var lnQuarter = Math.log(division);
        var u = lnN - lnQuarter;

        var tag = new DurationTag();

        while (Math.abs(u - approach) > accuracy) {
            int a = tag.getPlusCount();
            int b = tag.getMinusCount();
            int c = tag.getDotCount();

            var da = new double[] {
                    ((a + 1) - b) * ln2 + c * ln1_5,
                    (a - (b + 1)) * ln2 + c * ln1_5,
                    (a - b) * ln2 + (c + 1) * ln1_5
            };

            var difference = new double[] {
                    Math.abs(u - da[0]), // a + 1
                    Math.abs(u - da[1]), // b + 1
                    Math.abs(u - da[2]), // c + 1
            };

            int minIndex = minIndex(difference);

            tag.getTags()[minIndex] += 1;
            approach = da[minIndex];
        }

        // fix d = 1
        var tagDiv3 = new DurationTag();
        approach = .0;

        while (Math.abs(u - approach) > accuracy) {
            int a = tagDiv3.getPlusCount();
            int b = tagDiv3.getMinusCount();
            int c = tagDiv3.getDotCount();
            int d = tagDiv3.getDiv3Count();

            var da = new double[] {
                    ((a + 1) - b) * ln2 + c * ln1_5 - d * ln3,
                    (a - (b + 1)) * ln2 + c * ln1_5 - d * ln3,
                    (a - b) * ln2 + (c + 1) * ln1_5 - d * ln3,
                    (a - b) * ln2 + c * ln1_5 - (d + 1) * ln3
            };

            double[] difference;

            if (d >= 1) {
                difference = new double[] {
                        Math.abs(u - da[0]), // a + 1
                        Math.abs(u - da[1]), // b + 1
                        Math.abs(u - da[2]), // c + 1
                };
            } else {
                difference = new double[] {
                        Math.abs(u - da[0]), // a + 1
                        Math.abs(u - da[1]), // b + 1
                        Math.abs(u - da[2]), // c + 1
                        Math.abs(u - da[3]), // d + 1
                };
            }

            int minIndex = minIndex(difference);

            tagDiv3.getTags()[minIndex] += 1;
            approach = da[minIndex];
        }

        var d1 = Math.abs(tag.getValue() * division - ticks);
        var d2 = Math.abs(tagDiv3.getValue() * division - ticks);

        if (d1 < d2) return tag; else return tagDiv3;
    }

    public static DurationTag calculateSpecialDurationPro(long ticks, int division, double accuracy) {
        var approach = .0;
        var lnN = Math.log(ticks);
        var lnQuarter = Math.log(division);
        var u = lnN - lnQuarter;

        var tag = new DurationTag();

        while (Math.abs(u - approach) > accuracy) {
            int a = tag.getPlusCount();
            int b = tag.getMinusCount();
            int c = tag.getDotCount();
            int d = tag.getDiv3Count();
            int e = tag.getTags()[4];
            int f = tag.getTags()[5];
            int g = tag.getTags()[6];
            int h = tag.getTags()[7];
            int i = tag.getTags()[8];
            int j = tag.getTags()[9];
            int k = tag.getTags()[10]; // d
            int l = tag.getTags()[11]; // e
            int m = tag.getTags()[12]; // f
            int n = tag.getTags()[13]; // g
            int o = tag.getTags()[14]; // h
            int p = tag.getTags()[15]; // i
            int q = tag.getTags()[16]; // j

//            (a - b) * ln2 + c * ln1point5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
            var da = new double[] {
                    // (a - b) * ln2 + c * ln1point5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    ((a + 1) - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - (b + 1)) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + (c + 1) * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - (d + 1)) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - (e + 1)) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - (f + 1)) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - (g + 1)) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - (h + 1)) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - (i + 1)) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - (j + 1)) * ln9,
                    (a - b) * ln2 + c * ln1_5 + ((k + 1) - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + ((l + 1) - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + ((m + 1) - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + ((n + 1) - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + ((o + 1) - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + ((p + 1) - i) * ln8 + (q - j) * ln9,
                    (a - b) * ln2 + c * ln1_5 + (k - d) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + ((q + 1) - j) * ln9










//                    ((a + 1) - b) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - f * ln5 - g * ln6 - h * ln7 - i * ln8 - j * ln9,
//                    (a - (b + 1)) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - f * ln5 - g * ln6 - h * ln7 - i * ln8 - j * ln9,
//                    (a - b) * ln2 + (c + 1) * ln1point5 - d * ln3 - e * ln4 - f * ln5 - g * ln6 - h * ln7 - i * ln8 - j * ln9,
//                    (a - b) * ln2 + c * ln1point5 + (k - (d + 1)) * ln3 + (l - e) * ln4 + (m - f) * ln5 + (n - g) * ln6 + (o - h) * ln7 + (p - i) * ln8 + (q - j) * ln9,
//                    (a - b) * ln2 + c * ln1point5 - d * ln3 - (e + 1) * ln4 - f * ln5 - g * ln6 - h * ln7 - i * ln8 - j * ln9,
//                    (a - b) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - (f + 1) * ln5 - g * ln6 - h * ln7 - i * ln8 - j * ln9,
//                    (a - b) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - f * ln5 - (g + 1) * ln6 - h * ln7 - i * ln8 - j * ln9,
//                    (a - b) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - f * ln5 - g * ln6 - (h + 1) * ln7 - i * ln8 - j * ln9,
//                    (a - b) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - f * ln5 - g * ln6 - h * ln7 - (i + 1) * ln8 - j * ln9,
//                    (a - b) * ln2 + c * ln1point5 - d * ln3 - e * ln4 - f * ln5 - g * ln6 - h * ln7 - i * ln8 - (j + 1) * ln9,
            };

            var difference = new double[] {
                    Math.abs(u - da[0]),  // a + 1
                    Math.abs(u - da[1]),  // b + 1
                    Math.abs(u - da[2]),  // c + 1
                    Math.abs(u - da[3]),  // d + 1
                    Math.abs(u - da[4]),  // e + 1
                    Math.abs(u - da[5]),  // f + 1
                    Math.abs(u - da[6]),  // g + 1
                    Math.abs(u - da[7]),  // h + 1
                    Math.abs(u - da[8]),  // i + 1
                    Math.abs(u - da[9]),  // j + 1
                    Math.abs(u - da[10]), // k + 1
                    Math.abs(u - da[11]), // l + 1
                    Math.abs(u - da[12]), // m + 1
                    Math.abs(u - da[13]), // n + 1
                    Math.abs(u - da[14]), // o + 1
                    Math.abs(u - da[15]), // p + 1
                    Math.abs(u - da[16]), // q + 1
            };

            int minIndex = minIndex(difference);

            tag.getTags()[minIndex] += 1;
            approach = da[minIndex];
        }

        return tag;
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int minIndex(double[] data) {
        var newArray = Arrays.copyOf(data, data.length);
        Arrays.sort(newArray);

        for (int i = 0; i < data.length; i++) {
            if (newArray[0] == data[i])
                return i;
        }

        throw new RuntimeException("can not find index");
    }

    public static byte[] rest(byte[] data) {
        return rest(data, 1);
    }

    public static byte[] rest(byte[] data, int offset) {
        byte[] ret = new byte[data.length - offset];
        for (int i = offset, j = 0; i < data.length; i++, j++) {
            ret[j] = data[i];
        }
        return ret;
    }

    public static String toHex(byte data) {
        return toHex(new byte[]{data}, "");
    }

    public static String toHex(byte[] data) {
        return toHex(data, "");
    }

    public static String transferNoteName(byte code) {
        return transferNoteName(code, 4);
    }

    public static String transferNoteNameForChord(byte code) {
        return transferNoteNameForChord(code, 4);
    }

    public static String transferNoteNameForChord(byte code, int baseOctave) {
        return transferNoteName(code, baseOctave, false);
    }

    public static String transferNoteName(byte code, int baseOctave) {
        return transferNoteName(code, baseOctave, true);
    }

    public static String transferNoteName(byte code, int baseOctave, boolean front) {
        int noteOffset = code % 12;
        int octave = code / 12 - 1;
        String name;

        if (noteOffset < 0) throw new RuntimeException("mider has not supported this octave so far.");

        if (noteOffset == 0) {
            name = "c";
        } else if (noteOffset == 1) {
            if (front) name = "#c"; else name = "c\"";
        } else if (noteOffset == 2) {
            name = "d";
        } else if (noteOffset == 3) {
            if (front) name = "#d"; else name = "d\"";
        } else if (noteOffset == 4) {
            name = "e";
        } else if (noteOffset == 5) {
            name = "f";
        } else if (noteOffset == 6) {
            if (front) name = "#f"; else name = "f\"";
        } else if (noteOffset == 7) {
            name = "g";
        } else if (noteOffset == 8) {
            if (front) name = "#g"; else name = "g\"";
        } else if (noteOffset == 9) {
            name = "a";
        } else if (noteOffset == 10) {
            if (front) name = "#a"; else name = "a\"";
        } else {
//            noteOffset = 11;
            name = "b";
        }

        if (octave == baseOctave) return name;
        else if (octave == baseOctave + 1) return name.toUpperCase();
        else if (octave > baseOctave + 1) return name.toUpperCase() + octave;
        else return name + octave;
    }

    public static int tempo2bpm(int tempo) {
        return (60 * 1000000) / tempo;
    }

    public static boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static int byteArrayToInt(byte[] bytes) {

        var real = new byte[]{0, 0, 0, 0};

        for (int i = bytes.length - 1, j = 3; i >= 0 ; i--, j--) {
            real[j] = bytes[i];
        }

        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (real[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static String toHex(byte data, String limiter) {
        return toHex(new byte[] {data}, limiter);
    }

    public static String charMulti(char c, int count) {
        return String.valueOf(c).repeat(Math.max(0, count));
    }

    public static String toHex(byte[] data, String limiter) {
        var sb = new StringBuilder();

        for (byte b : data) {

            String hexNumber;

            if (b >= 0) {
                if (b <= 0xf)
                    hexNumber = "0" + Integer.toString(b, 16);
                else
                    hexNumber = Integer.toString(b, 16);
            } else {
                hexNumber = Integer.toHexString(b).substring(6, 8);
            }

            sb.append(hexNumber);
            sb.append(limiter);
        }

        return sb.toString().trim();
    }


    public static void a() {
        ArrayList<NoteMessage> noteMessages = new ArrayList<>();
        List<? extends NoteMessage> element = getSameHeadElement(noteMessages, HasMidiTicks::getMarkTicks);

    }

    public static Stream<? extends HasMidiTicks> sortedByMarkTicksStream(List<? extends HasMidiTicks> provide) {
        return provide.stream()
                .sorted(Comparator.comparingLong(HasMidiTicks::getMarkTicks));
    }

    public static Stream<? extends HasNoteCode> sortedByCodeStream(List<? extends HasNoteCode> provide) {
        return provide.stream()
                .sorted(Comparator.comparingLong(HasNoteCode::getCode));
    }

    public  static <T> List<? extends T> getSameHeadElement(List<? extends T> list, Function<T, Object> getValue) {
        ArrayList<T> retElement = new ArrayList<>();
//        long value = list.get(0).getMarkTicks();
        Object value = getValue.apply(list.get(0)); //list.get(0).getMarkTicks();
        for(var i : list) {
            if (getValue.apply(i).equals(value)) {
                retElement.add(i);
            } else break;
        }
        return retElement;
    }

    public static <R> R apply(R ref, Consumer<R> action) {
        action.accept(ref);
        return ref;
    }

    public static <T, R> R let(T ref, Function<T, R> action) {
        return action.apply(ref);
    }
}
