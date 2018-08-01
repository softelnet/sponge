/*
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.examples.PowerEchoAction
import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KKnowledgeBase

class Actions : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("scriptActionResult", null)
        sponge.setVariable("javaActionResult", null)
    }

    class EchoAction : KAction() {
        override fun onConfigure() {
            displayName = "Echo Action"
        }

        fun onCall(value: Number, text: String): Array<Any?> {
            return arrayOf(value, text)
        }
    }

    class ArrayArgumentAction : KAction() {
        fun onCall(arrayArg: Array<Any?>) = arrayArg.size;
    }

    override fun onLoad() {
        sponge.enableJava(PowerEchoAction::class.java)
    }

    override fun onStartup() {
        sponge.logger.debug("Calling script defined action")
        val scriptActionResult = sponge.call("EchoAction", 1, "test")
        sponge.logger.debug("Action returned: {}", scriptActionResult)
        sponge.setVariable("scriptActionResult", scriptActionResult)

        sponge.logger.debug("Calling Java defined action")
        val javaActionResult = sponge.call("PowerEchoAction", 1, "test")
        sponge.logger.debug("Action returned: {}", javaActionResult)
        sponge.setVariable("javaActionResult", javaActionResult)

        sponge.logger.debug("Disabling actions")
        sponge.disable(EchoAction::class)
        sponge.disableJava(PowerEchoAction::class.java)
    }
}

