package org.miditranser;

import org.miditranser.data.CalculateDurationConfiguration;
import org.miditranser.handle.StatusHandler;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MiderCodeTransfer {

    public static String parseMidiSource(InputStream stream) throws InvalidMidiDataException, IOException {
        return parseMidiSource(MidiSystem.getSequence(stream));
    }

    public static String parseMidiSource(Sequence src) {
        var miderCode = new ArrayList<String>();
        var cdc = new CalculateDurationConfiguration();
        cdc.setDivision(src.getResolution());
        cdc.setAccuracy(0.1);

        for (Track track : src.getTracks()) {
            var esm = new EventStateMachine(cdc);
            var lastDeltaTime = 0L;

            for (int i = 0; i < track.size(); i++) {
                var event = track.get(i);
                var deltaTime = event.getTick() - lastDeltaTime;
                var message = event.getMessage();
                var data = message.getMessage();
                var status = message.getStatus();

//                esm.handleMessage(status, rest(data), deltaTime);

                var handler = new StatusHandler(status, data, deltaTime, event.getTick());
                esm.initHandlerStackSettingOder(handler);
                handler.handle();

//                System.out.print("time: " + deltaTime + ", " + status + ", ");
//                System.out.println(Utils.toHex(data, " "));

                lastDeltaTime = event.getTick();
            }

            if (esm.hasNote()) {
                esm.generateList();
                miderCode.add(esm.getMiderTrackWriter().generateTrackCodeWithConfig());
            }

        }

        return miderCode.stream().filter(i->!i.isBlank()).collect(Collectors.joining("\n"));
    }
}
