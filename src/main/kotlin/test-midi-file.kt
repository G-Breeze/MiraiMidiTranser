package mider.utils

import org.miditranser.MiderCodeTransfer
import whiter.music.mider.code.produceCore
import whiter.music.mider.dsl.fromDslInstance
import whiter.music.mider.dsl.play
import whiter.music.mider.dsl.playDslInstance

fun main() {

//    val p = fromDsl {
//        bpm = 120
//        +("gEDCg+ G6--wm++ gEDCa+ G6--wm++ aFEDb+ G6--wm++ GGFDE+" +
//                " F6--vvv gEDCg+ G6--w^^ gEDCa+ F6--w^^ aFED G:b G:b G+:b+ " +
//                "A:D G:D F:d D:b C+:a+ A6--vvw E:C E:C E+:C+ E:C E:C E+:C+ EGC.D--E+ + F:a F:a F+:a+ F:a E:g E+:g+" +
//                " EDDCD+G+ E:C E:C E+:C+ E:C E:C E+:C+ EGC.D--E+ G6--w^^ F:a F:a F+:a+ F:a E:g E+:g+ GGFDC+")
//    }



    val code =  """
>160b;2x;4>(repeat 16:1↑2↑5↑1↑2↑5↑1↑2↑5↑1↑2↑5↑)(repeat 4:671↑671↑45645651↑3↑51↑3↑572↑572↑)
>160b;2x;3>(repeat 4:3++0++0++4++0++0++5++0++0++6++005++00)(repeat 4:6006004004001↑001↑00500500)
>160b;2x;6>0++033022011000000++000+00333022013003052001000++033042011000000++000+02222032011++000++000++033022011000000++000+00333022013003052001000++033042011000000++000+02222032011+0000++0055555055043021001023022005555055043023000212000001023040032011031020000001023040034050043022032011
    
    """.trimIndent()


    playDslInstance(miderDSL = produceCore(code).miderDSL)

//    playMiderCode(code)

//    m.save("src/main/resources/midis/test.mid")

//    val i = MidiSystem.getSequence(m.inStream())
//    val k = MidiSystem.getSequencer()
//    k.sequence = i
//    k.open()
//    k.start()
}

fun playMiderCode(code: String) {
    val result = produceCore(code)

    val midiFile = fromDslInstance(result.miderDSL)

    val parse = MiderCodeTransfer.parseMidiSource((midiFile.inStream()))

    println(parse)


    play {
//        +code
        bpm = 160*2
        parse.lines().forEach {
            track {
                +it
            }

        }
    }
}