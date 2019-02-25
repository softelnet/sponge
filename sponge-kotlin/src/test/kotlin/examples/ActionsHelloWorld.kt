/*
 * Sponge Knowledge base
 * Hello World action
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.type.StringType

class ActionsHelloWorld : KKnowledgeBase() {

    class HelloWorldAction : KAction() {
        override fun onConfigure() {
            withLabel("Hello world").withDescription("Returns a greeting text.")
            withArg(StringType("name").withLabel("Your name").withDescription("Type your name."))
            withResult(StringType().withLabel("Greeting").withDescription("The greeting text."))
        }

        fun onCall(name: String): String {
            return "Hello World! Hello $name!"
        }
    }

    override fun onStartup() {
        sponge.logger.info("{}", sponge.call("HelloWorldAction", listOf("Sponge user")))
    }
}
