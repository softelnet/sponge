/*
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.examples.PowerEchoAction
import org.openksavi.sponge.kotlin.KotlinAction
import org.openksavi.sponge.kotlin.KotlinKnowledgeBase

class Actions : KotlinKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("scriptActionResult", null)
        eps.setVariable("javaActionResult", null)
    }

    class EchoAction : KotlinAction() {
        override fun onConfigure() {
            displayName = "Echo Action"
        }

        override fun onCall(vararg args: Any?): Any? {
            logger.info("Action {} called", name)
            args.forEach { logger.debug("Arg: {} ({})", it, it?.javaClass) }
            return args
        }
    }

    override fun onLoad() {
        eps.enableJava(PowerEchoAction::class.java)
    }

    override fun onStartup() {
        eps.logger.debug("Calling script defined action")
        // The action name coud be specified as EchoAction::class.qualifiedName
        val scriptActionResult = eps.call("org.openksavi.sponge.kotlin.examples.Actions.EchoAction", 1, "test")
        eps.logger.debug("Action returned: {}", scriptActionResult)
        eps.setVariable("scriptActionResult", scriptActionResult)

        eps.logger.debug("Calling Java defined action")
        val javaActionResult = eps.call("org.openksavi.sponge.examples.PowerEchoAction", 1, "test")
        eps.logger.debug("Action returned: {}", javaActionResult)
        eps.setVariable("javaActionResult", javaActionResult)

        eps.logger.debug("Disabling actions")
        eps.disable(EchoAction::class)
        eps.disableJava(PowerEchoAction::class.java)
    }
}

