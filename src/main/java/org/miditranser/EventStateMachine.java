package org.miditranser;

import org.miditranser.data.Addable;
import org.miditranser.data.Controller;
import org.miditranser.data.NoneOccupyTicksContainer;
import org.miditranser.data.ProgramChange;
import org.miditranser.data.midi.message.ControllerMessage;
import org.miditranser.data.midi.message.HasMidiTicks;
import org.miditranser.data.midi.message.NoteMessage;
import org.miditranser.data.midi.message.ProgramChangeMessage;
import org.miditranser.handle.AbstractHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import static org.miditranser.Utils.*;

public class EventStateMachine {

    private final MiderTrackWriter mtw;
    private final Stack<NoteMessage> messageStack = new Stack<>();
    private final List<List<? extends HasMidiTicks>> firstIterationStack = new ArrayList<>();
    private final List<NoteMessage> bufferIterationList = new ArrayList<>();

    EventStateMachine(MiderTrackWriter mtw) {
        this.mtw = mtw;
    }

    public EventStateMachine(int division) {
        this(new MiderTrackWriter(division));
    }

    public List<List<? extends HasMidiTicks>> getFirstIterationStack() {
        return firstIterationStack;
    }

    private int counter;

    public void stackHandleMessageSettingOder(AbstractHandler handler) {
        handler.handleNoteOn(msg -> {
            msg.setOrder(counter ++);
            messageStack.push(msg);
        }).handleNoteOff(msg -> {

            msg.setOrder(counter ++);
            bufferIterationList.add(messageStack.pop());
            bufferIterationList.add(msg);

            if (messageStack.isEmpty()) {
                firstIterationStack.add(new ArrayList<>(bufferIterationList));
                bufferIterationList.clear();
            }
        }).handleAfterTouch(msg -> {
            var list = new ArrayList<NoteMessage>();
            list.add(msg);
            firstIterationStack.add(list);
        }).handleMeta(meta -> {
            if (meta.getType() == 0x51)
                mtw.setBpm(tempo2bpm(byteArrayToInt(meta.getData())));
        }).handleProgramChange(msg -> {
            firstIterationStack.add(new NoneOccupyTicksContainer(msg));
        });
    }

    public void stackHandleMessage(AbstractHandler handler) {
        handler.handleNoteOn(messageStack::push).handleNoteOff(msg -> {

            bufferIterationList.add(messageStack.pop());
            bufferIterationList.add(msg);

            if (messageStack.isEmpty()) {
                firstIterationStack.add(new ArrayList<>(bufferIterationList));
                bufferIterationList.clear();
            }
        }).handleAfterTouch(msg -> {
            var list = new ArrayList<NoteMessage>();
            list.add(msg);
            firstIterationStack.add(list);
        }).handleMeta(meta -> {
            if (meta.getType() == 0x51)
                mtw.setBpm(tempo2bpm(byteArrayToInt(meta.getData())));
        }).handleProgramChange(msg -> {
            firstIterationStack.add(new NoneOccupyTicksContainer(msg));
        });
    }

    public boolean hasNote() {
        return firstIterationStack.size() != 0;
    }

    public void gen() {
        for (var events : firstIterationStack) {
            if (events instanceof NoneOccupyTicksContainer) {
                HasMidiTicks message = ((NoneOccupyTicksContainer) events).getMessage();

                if (message instanceof ProgramChangeMessage) {
                    mtw.addNoneSound(new ProgramChange(((ProgramChangeMessage) message)));
                } else if (message instanceof ControllerMessage) {
                    mtw.addNoneSound(new Controller(((ControllerMessage) message)));
                }

            } else mtw.addSound(((List<NoteMessage>) events));
        }
    }

    public MiderTrackWriter getMiderTrackWriter() {
        return mtw;
    }
}
