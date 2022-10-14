package org.miditranser;

import java.util.ArrayList;
import java.util.HashMap;
public class ToMidercode {
    // TODO Make plurality tracks to play
    private int tracks = 0;
    private final long quarNoteTicks;
    private final long bpm;
    private ArrayList<Object> times;
    private ArrayList<Object> events;
    private ArrayList<Object> notes;
    private ArrayList<Object> powers;
    private HashMap<Integer, String> notesMap = new HashMap<>();
    public ToMidercode(long quarNoteTicks, long bpm, ArrayList<Object> times, ArrayList<Object> events, ArrayList<Object> notes, ArrayList<Object> powers) {
        this.quarNoteTicks = quarNoteTicks;
        this.bpm = bpm;
        this.times = times;
        this.events = events;
        this.notes = notes;
        this.powers = powers;
    }
    public String getMidercode() {
        noteMapInit();
        StringBuilder miderCode = new StringBuilder(getBpm() + ">");
        long[] midievent = new long[4];
        for (int i=0;i< times.size();i++) {
            midievent[0] = Long.parseLong(times.get(i).toString());
            midievent[1] = Long.parseLong(events.get(i).toString());
            midievent[2] = Long.parseLong(notes.get(i).toString());
            midievent[3] = Long.parseLong(powers.get(i).toString());
            miderCode.append(toMidercode(midievent));
        }
        return miderCode.toString();
    }
    private String getBpm() {
        String bpmCode = ">" + 60000000/bpm + "b";
        return bpmCode;
    }
    private String toMidercode(long[] event) {
        StringBuilder resultCode = new StringBuilder();
        long deltatime = event[0];
        int noteNum = (int)event[2];
        if (event[1] >= 144 && event[1] <= 159) {
            String note = notesMap.get(noteNum%12);
            int octave = (noteNum - (noteNum%12))/12 - 1;
            resultCode.append(note).append(octave);
            if (event[3] != 100) {
                resultCode.append("%").append(event[3]);
            }
            if (deltatime != quarNoteTicks && deltatime > 0) {
                // TODO 量化ticks
                double magnification = (double) deltatime/quarNoteTicks;
                String symbol = "+";
                if (magnification < 1) {
                    magnification = (double) quarNoteTicks/deltatime;
                    symbol = "-";
                }
                while (magnification >= 2.0) {
                    magnification /=2.0;
                    resultCode.append(symbol);
                    }
                while (magnification >= 1.125) {
                    magnification /=1.5;
                    resultCode.append(".");
                }
            }
        }
        return resultCode.toString();
    }
    private void noteMapInit() {
        notesMap.put(0, "c");
        notesMap.put(1, "#c");
        notesMap.put(2, "d");
        notesMap.put(3, "#d");
        notesMap.put(4, "e");
        notesMap.put(5, "f");
        notesMap.put(6, "#f");
        notesMap.put(7, "g");
        notesMap.put(8, "#g");
        notesMap.put(9, "a");
        notesMap.put(10, "#a");
        notesMap.put(11, "b");
    }
    public ArrayList getTimes() {
        return times;
    }
    public void setTimes(ArrayList times) {
        this.times = times;
    }
    public ArrayList getEvents() {
        return events;
    }
    public void setEvents(ArrayList events) {
        this.events = events;
    }
    public ArrayList getNotes() {
        return notes;
    }
    public void setNotes(ArrayList notes) {
        this.notes = notes;
    }
    public ArrayList getPowers() {
        return powers;
    }
    public void setPowers(ArrayList powers) {
        this.powers = powers;
    }
}
