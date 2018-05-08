/*
 * Sponge Knowledge base
 * Processor inheritance
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KKnowledgeBase

class ProcessorInheritance : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("result", null)
    }

    abstract class AbstractEchoAction : KAction() {
        open fun calculateResult(args: Array<Any?>) = 1
    }

    class EchoAction : AbstractEchoAction() {
        override fun onCall(args: Array<Any?>) = calculateResult(args) * 2
    }

    override fun onStartup() {
        var result = eps.call("EchoAction")
        eps.setVariable("result", result)
        logger.debug("Action returned: {}", result)
    }
}
