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

class HelloWorldAction : KAction() {
    override fun onConfigure() {
        label = "Hello world"
        description = "Returns a greeting text."
        argsMeta = listOf(ArgMeta("name", StringType()).withLabel("Your name").withDescription("Type your name."))
        resultMeta = ResultMeta(StringType()).withLabel("Greeting").withDescription("The greeting text.")
    }

    fun onCall(name: String): String {
        return "Hello World! Hello $name!"
    }
}

override fun onStartup() {
    sponge.logger.info("{}", sponge.call("HelloWorldAction", listOf("Sponge user")))
}
