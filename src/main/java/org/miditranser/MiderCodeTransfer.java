package org.miditranser;

import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.handle.StatusHandler;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.miditranser.Utils.rest;

public class MiderCodeTransfer {

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        String path = Objects.requireNonNull(Main.class.getResource("/midis/tiankongzhicheng.mid")).getPath();
//        String path = Objects.requireNonNull(Main.class.getResource("/midis/nexttoyou.mid")).getPath();
        var seq = MidiSystem.getSequence(new File(path));
        String parse = parse(seq);
        System.out.println(parse);
    }

    public static String parse(InputStream stream) throws InvalidMidiDataException, IOException {
        return parse(MidiSystem.getSequence(stream));
    }

    private static String parse(Sequence src) {
        var miderCode = new ArrayList<String>();

        for (Track track : src.getTracks()) {
            var esm = new EventStateMachine(src.getResolution());
            var lastDeltaTime = 0L;

            for (int i = 0; i < track.size(); i++) {
                var event = track.get(i);
                var deltaTime = event.getTick() - lastDeltaTime;
                var message = event.getMessage();
                var data = message.getMessage();
                var status = message.getStatus();

//                esm.handleMessage(status, rest(data), deltaTime);

                var handler = new StatusHandler(status, data, deltaTime, event.getTick());
                esm.stackHandleMessageSettingOder(handler);
                handler.handle();

//                System.out.print("time: " + deltaTime + ", " + status + ", ");
//                System.out.println(Utils.toHex(data, " "));

                lastDeltaTime = event.getTick();
            }

            if (esm.hasNote()) {
//                esm.test();
                esm.gen();

                // String code = esm.getMiderTrackWriter().getTrackCode();
//                System.out.println(code);
                miderCode.add(esm.getMiderTrackWriter().getTrackCodeWithConfig());
            }

        }

        return String.join("\n", miderCode);
    }
}
