/*
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

import org.openksavi.sponge.examples.PowerEchoAction

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("scriptActionResult", null)
    sponge.setVariable("javaActionResult", null)
}

class EchoAction : Action() {
    override fun onConfigure() {
        label = "Echo Action"
    }

    fun onCall(value: Number, text: String): Array<Any?> {
        return arrayOf(value, text)
    }
}

class ArrayArgumentAction : Action() {
    fun onCall(arrayArg: Array<Any?>) = arrayArg.size;
}

fun onLoad() {
    sponge.enableJava(PowerEchoAction::class.java)
}

fun onStartup() {
    sponge.logger.debug("Calling script defined action")
    val scriptActionResult = sponge.call("EchoAction", listOf(1, "test"))
    sponge.logger.debug("Action returned: {}", scriptActionResult)
    sponge.setVariable("scriptActionResult", scriptActionResult)

    sponge.logger.debug("Calling Java defined action")
    val javaActionResult = sponge.call("PowerEchoAction", listOf(1, "test"))
    sponge.logger.debug("Action returned: {}", javaActionResult)
    sponge.setVariable("javaActionResult", javaActionResult)

    sponge.logger.debug("Disabling actions")
    sponge.disable(EchoAction::class)
    sponge.disableJava(PowerEchoAction::class.java)
}

