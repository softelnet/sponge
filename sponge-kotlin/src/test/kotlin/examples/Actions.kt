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
        eps.setVariable("scriptActionResult", null)
        eps.setVariable("javaActionResult", null)
    }

    class EchoAction : KAction() {
        override fun onConfigure() {
            displayName = "Echo Action"
        }

        override fun onCall(vararg args: Any?): Any? {
            logger.info("Action {} called", name)
            args.forEach { logger.debug("Arg: $it (${it?.javaClass})") }
            return args
        }
    }

    override fun onLoad() {
        eps.enableJava(PowerEchoAction::class.java)
    }

    override fun onStartup() {
        eps.logger.debug("Calling script defined action")
        val scriptActionResult = eps.call("EchoAction", 1, "test")
        eps.logger.debug("Action returned: {}", scriptActionResult)
        eps.setVariable("scriptActionResult", scriptActionResult)

        eps.logger.debug("Calling Java defined action")
        val javaActionResult = eps.call("PowerEchoAction", 1, "test")
        eps.logger.debug("Action returned: {}", javaActionResult)
        eps.setVariable("javaActionResult", javaActionResult)

        eps.logger.debug("Disabling actions")
        eps.disable(EchoAction::class)
        eps.disableJava(PowerEchoAction::class.java)
    }
}

