package org.miditranser;

import org.miditranser.data.*;
import org.miditranser.data.midi.message.*;
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

    public EventStateMachine(CalculateDurationConfiguration cdc) {
        this(new MiderTrackWriter(cdc));
    }

    private List<List<? extends HasMidiTicks>> getFirstIterationStack() {
        return firstIterationStack;
    }

    private int counter;

    public void initHandlerStackSettingOder(AbstractHandler handler) {
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
            firstIterationStack.add(new NoneOccupyTicksContainer(meta));
        }).handleProgramChange(msg -> {
            firstIterationStack.add(new NoneOccupyTicksContainer(msg));
        }).handleController(msg->{
            firstIterationStack.add(new NoneOccupyTicksContainer(msg));
        });
    }

    public void initHandlerStack(AbstractHandler handler) {
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
            if (meta.getType() == 0x51) {
                mtw.setBpm(tempo2bpm(byteArrayToInt(meta.getData())));
            }
        }).handleProgramChange(msg -> {
            firstIterationStack.add(new NoneOccupyTicksContainer(msg));
        }).handleController(msg->{
            firstIterationStack.add(new NoneOccupyTicksContainer(msg));
        });
    }

    public boolean hasNote() {
        return firstIterationStack.size() != 0;
    }

    public void generateList() {
        for (var events : firstIterationStack) {
            if (events instanceof NoneOccupyTicksContainer) {
                HasMidiTicks message = ((NoneOccupyTicksContainer) events).getMessage();

                if (message instanceof ProgramChangeMessage) {
                    mtw.addNoneSound(new ProgramChange(((ProgramChangeMessage) message)));
                } else if (message instanceof ControllerMessage) {
                    mtw.addNoneSound(new Controller(((ControllerMessage) message)));
                } else if (message instanceof MetaMessage) {
                    mtw.addNoneSound(new Meta(((MetaMessage) message)));
                }

            } else mtw.addSound(((List<NoteMessage>) events));
        }
    }

    public MiderTrackWriter getMiderTrackWriter() {
        return mtw;
    }
}
