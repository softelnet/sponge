/*
 * Sponge Knowledge Base
 * Actions - Java inheritance
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.examples.PowerEchoAction
import org.openksavi.sponge.kotlin.KKnowledgeBase

class ActionsJavaInheritance : KKnowledgeBase() {

    class ExtendedFromAction : PowerEchoAction() {
        override fun onCall(value: Number, text: String): List<Any?> {
            return listOf(value.toInt() + 10, text.toLowerCase())
        }
    }
}

