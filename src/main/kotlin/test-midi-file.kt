package mider.utils

import org.miditranser.MiderCodeTransfer
import whiter.music.mider.MidiFile
import whiter.music.mider.MidiNote
import whiter.music.mider.code.produceCore
import whiter.music.mider.dsl.fromDsl
import whiter.music.mider.dsl.fromDslInstance
import whiter.music.mider.dsl.play
import java.io.File
import javax.sound.midi.MidiSystem

fun main() {

//    val p = fromDsl {
//        bpm = 120
//        +("gEDCg+ G6--wm++ gEDCa+ G6--wm++ aFEDb+ G6--wm++ GGFDE+" +
//                " F6--vvv gEDCg+ G6--w^^ gEDCa+ F6--w^^ aFED G:b G:b G+:b+ " +
//                "A:D G:D F:d D:b C+:a+ A6--vvw E:C E:C E+:C+ E:C E:C E+:C+ EGC.D--E+ + F:a F:a F+:a+ F:a E:g E+:g+" +
//                " EDDCD+G+ E:C E:C E+:C+ E:C E:C E+:C+ EGC.D--E+ G6--w^^ F:a F:a F+:a+ F:a E:g E+:g+ GGFDC+")
//    }



    val code = ">g>{onC}a{offC,0}"

    val result = produceCore(code)

    val midiFile = fromDslInstance(result.miderDSL)

    val parse = MiderCodeTransfer.parse((midiFile.inStream()))

    println(parse)

//    play {
//        +parse
//    }

//    m.save("src/main/resources/midis/test.mid")

//    val i = MidiSystem.getSequence(m.inStream())
//    val k = MidiSystem.getSequencer()
//    k.sequence = i
//    k.open()
//    k.start()
}