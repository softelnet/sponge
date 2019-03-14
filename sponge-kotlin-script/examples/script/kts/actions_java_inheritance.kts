/*
 * Sponge Knowledge base
 * Actions - Java inheritance
 */

import org.openksavi.sponge.examples.PowerEchoAction

class ExtendedFromAction : PowerEchoAction() {
    override fun onCall(value: Number, text: String): List<Any?> {
        return listOf(value.toInt() + 10, text.toLowerCase())
    }
}
