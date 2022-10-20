package org.miditranser.data;

interface Chord extends FromMidiEvent {

//    public Chord(List<? extends NoteMessage> list) {
//        super(list);
////        this.codes = codes;
////        this.onVelocities = onVelocities;
////        this.offVelocities = offVelocities;
////        this.onChannels = onChannels;
////        this.offChannels = offChannels;
////        this.onsTicks = onsTicks;
////        this.offsTicks = offsTicks;
//    }

//    public Chord(List<NoteOnMessage> ons, List<NoteOffMessage> offs) {
////        var onStream = ons.stream();
////        var offStream = offs.stream();
////        var codes = onStream.;
////        var onV = onStream.map(NoteMessage::getVelocity).collect(Collectors.toList());
////        var onC = onStream.map(NoteMessage::getChannel).collect(Collectors.toList());
////        var offV = offStream.map(NoteMessage::getVelocity).collect(Collectors.toList());
////        var offC = offStream.map(NoteMessage::getChannel).collect(Collectors.toList());
//        this(
//                ons.stream().map(NoteOnMessage::getCode).collect(Collectors.toList()),
//                ons.stream().map(NoteOnMessage::getVelocity).collect(Collectors.toList()),
//                offs.stream().map(NoteOffMessage::getVelocity).collect(Collectors.toList()),
//                ons.stream().map(NoteOnMessage::getChannel).collect(Collectors.toList()),
//                offs.stream().map(NoteOffMessage::getChannel).collect(Collectors.toList()),
//                ons.stream().map(NoteOnMessage::getMarkTicks).collect(Collectors.toList()),
//                offs.stream().map(NoteOffMessage::getMarkTicks).collect(Collectors.toList())
//        );
//    }
}
