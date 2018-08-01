/*
 * Sponge Knowledge base
 * Processor inheritance
 */

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("result", null)
}

abstract class AbstractEchoAction : Action() {
    open fun calculateResult() = 1
}

class EchoAction : AbstractEchoAction() {
    fun onCall() = calculateResult() * 2
}

fun onStartup() {
    var result = sponge.call("EchoAction")
    sponge.setVariable("result", result)
    sponge.logger.debug("Action returned: {}", result)
}
