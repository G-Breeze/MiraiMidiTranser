package org.miditranser;

import org.miditranser.data.NoneOccupyTicksContainer;
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

    EventStateMachine(MiderTrackWriter mtw) {
        this.mtw = mtw;
    }

    public EventStateMachine(int division) {
        this(new MiderTrackWriter(division));
    }

//    private String state = "detect";
//    private String fromsState;
//    private Object passingObject;
//    private long passingDeltaTime;
//    private byte passingNoteCode;
//    private byte passingVelocity;
//    private final Stack<NoteMessage> chordMessageStack = new Stack<>();
//
//    private int c = 1;
//    public void handleMessage(int midiStatus, byte[] data, long deltaTime) {
//
//
////        System.out.print((c ++) + ". ");
////        System.out.print("status: ");
////        System.out.print(toHex((byte) midiStatus));
////        System.out.print(", delta: ");
////        System.out.print(deltaTime);
////        System.out.print(", data: ");
////        System.out.println(toHex(data, " ").toUpperCase());
//
////        var sb = new StringBuilder();
////        sb.append("hex(\"");
////        sb.append(toHex(asvlByteArray((int) deltaTime), " "));
////        sb.append(toHex((byte) midiStatus));
////        sb.append(" ");
////        sb.append(toHex(data, " ").toUpperCase().trim());
////        sb.append("\")");
////        System.out.println(sb.toString().trim());
//
//        var hh = new StatusHandler(midiStatus).handleNoteOn(msg -> {
//            chordMessageStack.push(msg);
//            System.out.print(">in  " + transferNoteName(msg.getCode()) + ", delta: " + deltaTime + ", velocity: " + msg.getVelocity());
//        }).handleNoteOff(msg -> {
//            chordMessageStack.pop();
//            System.out.print("<out " + transferNoteName(msg.getCode()) + ", delta: " + deltaTime + ", velocity: " + msg.getVelocity());
//        });
//
//        System.out.println(", size: " + chordMessageStack.size());
//
//        hh.handle();
//
//        if (is("detect")) {
//
//            new StatusHandler(midiStatus).handleMeta(meta -> {
//                if (meta.getType() == 0x51) {
//                    int bpm = tempo2bpm(byteArrayToInt(meta.getData()));
//                    mtw.setBpm(bpm);
//                    System.out.println("set bpm: " + bpm);
//                }
//            }).handleNoteOn(msg -> {
//                state = "noteOnStage";
//                fromsState = "detect";
//                passingObject = msg;
//            }).handleNoteOff(msg -> {
//
//            }).handleProgramChange(msg -> {
//
//            }).handleGlissando(msg -> {
//
//            }).handle(data, deltaTime);
//
////            if (midiStatus == 0xff) {
////                // assume data use 1 byte to store length
////                var type = data[0];
////                var content = rest(data, 2);
////
////                if (type == 0x51) {
////                    // parse bpm
////                    int bpm = tempo2bpm(byteArrayToInt(content));
////                    mtw.setBpm(bpm);
////                    System.out.println("set bpm: " + bpm);
////                } else {
////                    // todo not yet implement
////                }
////            } else if (0x90 <= midiStatus && midiStatus <= 0x9f) {
////                var name = transferNoteName(data[0]);
////                var velocity = Integer.toString(data[1], 10);
////                state = "noteOnStage";
////
////                passingDeltaTime = deltaTime;
////                passingVelocity = data[1];
////                passingNoteCode = data[0];
////
////                if (deltaTime > 0) {
////
////                } else {
////
////                }
////
////                if (deltaTime > 0)
////                    System.out.println("note on: " + name + ", " + velocity + ", " + calculateDuration(deltaTime, 960));
////                else System.out.println("note on: 0");
////            } else if (0x80 <= midiStatus && midiStatus <= 0x8f) {
////                var name = transferNoteName(data[0]);
////                var velocity = Integer.toString(data[1], 10);
////                System.out.println("note off: " + name + ", " + velocity);
////            } else if (0xe0 <= midiStatus && midiStatus <= 0xef) {
////                System.out.println("滑音");
////            } else if (0xc0 <= midiStatus && midiStatus <= 0xcf) {
////                System.out.println("乐器改变");
////            } else if (0xf0 <= midiStatus && midiStatus < 0xff) {
////                System.out.println("system code");
////            } else if (0xa0 <= midiStatus && midiStatus < 0xaf) {
////                System.out.println("触后音符");
////            } else if (0xb0 <= midiStatus && midiStatus < 0xbf) {
////                System.out.println("controller");
////            } else if (0xd0 <= midiStatus && midiStatus < 0xdf) {
////                System.out.println("触后通道");
////            }
//        } else if (is("noteOnStage")) {
//
//            var generate = new Function<NoteMessage>() {
//
//                @Override
//                public void invoke(NoteMessage msg) {
//                    var on = (NoteOnMessage) passingObject;
//                    checkOnAndOffMessage(on, msg);
//
//                    var time = Math.abs(on.getDeltaTime() - msg.getDeltaTime());
//                    mtw.addSingleNote(time, msg.getCode(), msg.getChannel());
//                    var name = transferNoteName(msg.getCode());
//                    var tag = calculateDuration(time, 480);
//
////                    System.out.println(name + ", " + tag);
//                    state = "detect";
//                }
//            };
//
//            new StatusHandler(midiStatus).handleNoteOn(msg -> {
//                if (msg.getVelocity()  == 0) {
//                    generate.invoke(msg);
//                } else {
//                    // chord processing
//                    state = "chordStage";
////                    chordMessageStack.push(msg);
//                }
//            }).handleNoteOff(generate::invoke).handle(data, deltaTime);
//        } else if (is("chordStage")) {
//            new StatusHandler(midiStatus).handleNoteOn(msg -> {
////                chordMessageStack.push(msg);
//                if (msg.getVelocity() != 0) {
////                     var timeWithLast = chordMessageStack.peek().getDeltaTime() - msg.getDeltaTime();
////                     if (timeWithLast == 0) {
////                          chordMessageStack.push(msg);
////                     }
//                } else {
//
//                }
//            }).handleNoteOff(msg -> {
//
////                chordMessageStack.pop();
//            }).handle(data, deltaTime);
//        } if (is("end")) {
//
//        }
//    }

    private final Stack<NoteMessage> messageStack = new Stack<>();

    public List<List<? extends HasMidiTicks>> getFirstIterationStack() {
        return firstIterationStack;
    }

    private final List<List<? extends HasMidiTicks>> firstIterationStack = new ArrayList<>();
    private final List<NoteMessage> bufferIterationList = new ArrayList<>();


    private int counter;


    public void stackHandleMessageSettingOder(AbstractHandler handler) {
        handler.handleNoteOn(msg->{
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

//    private boolean is(String s) {
//        return eq(state, s);
//    }
//
//    private boolean from(String s) {
//        return eq(fromsState, s);
//    }
//
//    private static void checkOnAndOffMessage(NoteMessage a, NoteMessage b) {
//        if (a.getChannel() != b.getChannel() && a.getCode() != b.getCode())
//            throw new RuntimeException("not paired noteOn and noteOff");
//    }

    public boolean hasNote() {
        return firstIterationStack.size() != 0;
    }

    public void gen() {

        for (var events : firstIterationStack) {
            if (events instanceof NoneOccupyTicksContainer) {
                HasMidiTicks message = ((NoneOccupyTicksContainer) events).getMessage();
                if (message instanceof ProgramChangeMessage) {
                    ((ProgramChangeMessage) message).getInstrument();
                }
            } else {
//                var occupyTicks = ((List<? extends NoteMessage>) events);
                mtw.addSound(((List<NoteMessage>) events));
//
//                if (occupyTicks.size() == 2) {
//                    // paired
//                    // mtw.addCombinedNote(occupyTicks.get(0), occupyTicks.get(1));
//                } else if (events.size() > 2) {
//
//                    // mtw.addChord((List<NoteMessage>) occupyTicks);
//
////                    var r = MiderTrackWriter.checkChord(
////                            occupyTicks.stream().filter(NoteMessage::isNoteOn).collect(Collectors.toList()),
////                            occupyTicks.stream().filter(NoteMessage::isNoteOff).collect(Collectors.toList())
////                    );
////
////                    if (MiderTrackWriter.isStanderChord(r)) {
////                        mtw.addCombinedChord(
////                                occupyTicks.stream().filter(NoteMessage::isNoteOn).collect(Collectors.toList()),
////                                occupyTicks.stream().filter(NoteMessage::isNoteOff).collect(Collectors.toList()));
////                    }
//                }
            }

        }
//        mtw.addRest();
    }

    public MiderTrackWriter getMiderTrackWriter() {
        return mtw;
    }

    public void test() {
//        System.out.println(firstIterationStack);
//        firstIterationStack.stream().filter(l -> true).forEach(e -> {
//            System.out.println("** " + e.size() + " > " + e.stream().map(p -> {
//                String name = "err";
//                if (p.isNoteOn()) name = "i" + transferNoteName(p.getCode());
//                else if (p.isNoteOff()) name = "o" + transferNoteName(p.getCode());
//                return name;
//            }).collect(Collectors.joining(" ")) );
//        });
    }
}
