package org.miditranser;

import mider.utils.Wrap;

import javax.sound.midi.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        String resMidercode = "";
        ArrayList<Object> ticks = new ArrayList<>();
        ArrayList<Object> times;
        ArrayList<Object> events = new ArrayList<>();
        ArrayList<Object> notes = new ArrayList<>();
        ArrayList<Object> powers = new ArrayList<>();
        String path = Objects.requireNonNull(Main.class.getResource("/midis/tiankongzhicheng.mid")).getPath();
        // get the midi file
        try {
            Sequence seq = MidiSystem.getSequence(new File(path));
            Sequencer sequencer = MidiSystem.getSequencer();
            if (sequencer == null) {
                throw new IOException("未找到可用音序器");
            }
            // quarter note ticks
            long qtNoteTick = seq.getResolution();

            // bpmMs
            Track[] tracks = seq.getTracks();
            long bpmMs = getBpmms(tracks);
            //System.out.println(bpmMs);

            for (Track t: tracks) {
                for (int i=0;i<t.size();i++) {
                    MidiEvent midiEvent = t.get(i);
                    MidiMessage message = midiEvent.getMessage();
                    long tick = midiEvent.getTick();
                    byte[] data = message.getMessage();
                    message.getStatus();
                    // show the datas
//                    for (int j=0;j< data.length;j++) {
//                        System.out.print(Integer.toString(data[j], 16) + " ");
//                    }
//                    System.out.println();
                    if (i > 0 && i<t.size()-1 && data.length >= 3) {
                        ticks.add(tick);
                        events.add(data[0]*-1 + 16);
                        notes.add(data[1]);
                        powers.add(data[2]);
                    }
                }
            }

            // get deltatime
            times = toDeltatime(ticks);

            // show MIDIevents
            //showEvents(times, events, notes, powers);

            // play the midi
//            sequencer.setSequence(seq);
//            sequencer.open();
//            sequencer.start();

            // Convert to midercode
            ToMidercode transer = new ToMidercode(qtNoteTick, bpmMs, times, events, notes, powers);
            resMidercode = transer.getMidercode();
            System.out.println(resMidercode);
        } catch (InvalidMidiDataException | IOException | MidiUnavailableException e) {
            throw new RuntimeException(e);
        }
        // play by midercode
        Wrap.play(resMidercode);
    }

    private static ArrayList toDeltatime(ArrayList ticks) {
        ArrayList<Object> times = new ArrayList<>();
        for (int i=0;i< ticks.size();i++) {
            // delta-time
            long deltatime =(i == 0)?-1:(long) ticks.get(i);
            if (i>0) {
                deltatime -= (long)ticks.get(i-1);
            }
            times.add(deltatime);
        }
        return times;
    }
    private static void showEvents(ArrayList times, ArrayList events, ArrayList notes, ArrayList powers) {
        for (int i=0;i< times.size();i++) {
            // MIDIevent
            // delta-time
            System.out.print("deltatime:" + times.get(i) + " ");
            // event
            System.out.print("event:" + events.get(i) + " ");
            // note's index
            System.out.print("note:" + notes.get(i) + " ");
            // note power
            System.out.print("power:" + powers.get(i));
            System.out.println();
        }
    }
    private static long getBpmms(Track[] tracks) {
        int nums = 0;
        int index = 0;
        for (int i=0;i<3;i++) {
            if (Integer.toString(tracks[0].get(i).getMessage().getMessage()[1], 16).equals("51")) {
                nums = tracks[0].get(i).getMessage().getMessage()[2];
                index = i;
                break;
            }
        }
        System.out.println(nums);
        String bpmStr = "";
        for (int i=3;i<3+nums;i++) {
            String num = Integer.toString(tracks[0].get(index).getMessage().getMessage()[i], 16);
            if (num.startsWith("-")) {
                bpmStr += Integer.toString((0xff-(~Byte.parseByte(num, 16))), 16);
            } else {
                bpmStr += num;
            }
        }
        long bpmMs = Long.parseLong(bpmStr, 16);
        return bpmMs;
    }
}