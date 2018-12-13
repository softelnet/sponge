/*
 * Sponge Knowledge base
 * Hello World action
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.action.ArgMeta
import org.openksavi.sponge.action.ResultMeta
import org.openksavi.sponge.kotlin.KAction
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.type.StringType

class ActionsHelloWorld : KKnowledgeBase() {

    class HelloWorldAction : KAction() {
        override fun onConfigure() {
            displayName = "Hello world"
            description = "Returns a greeting text."
            argsMeta = listOf(ArgMeta("name", StringType()).displayName("Your name").description("Type your name."))
            resultMeta = ResultMeta(StringType()).displayName("Greeting").description("The greeting text.")
        }

        fun onCall(name: String): String {
            return "Hello World! Hello $name!"
        }
    }

    override fun onStartup() {
        sponge.logger.info("{}", sponge.call("HelloWorldAction", listOf("Sponge user")))
    }
}
