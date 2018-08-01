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
        sponge.setVariable("result", null)
    }

    abstract class AbstractEchoAction : KAction() {
        open fun calculateResult() = 1
    }

    class EchoAction : AbstractEchoAction() {
        fun onCall() = calculateResult() * 2
    }

    override fun onStartup() {
        var result = sponge.call("EchoAction")
        sponge.setVariable("result", result)
        logger.debug("Action returned: {}", result)
    }
}
