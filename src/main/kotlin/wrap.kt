package mider.utils

import whiter.music.mider.code.produceCore
import whiter.music.mider.dsl.playDslInstance

object Wrap {
    @JvmStatic
    fun play(code: String) {
//        listOf("").joinToString()
        playDslInstance(true, produceCore(code).miderDSL)
    }
}