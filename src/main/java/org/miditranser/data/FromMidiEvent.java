package org.miditranser.data;

public interface FromMidiEvent extends Addable {
    long getHeadTicks();
    long getTailTicks();
}
