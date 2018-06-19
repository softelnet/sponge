/*
 * Sponge Knowledge base
 * Processor inheritance
 */

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("result", null)
}

abstract class AbstractEchoAction : Action() {
    open fun calculateResult() = 1
}

class EchoAction : AbstractEchoAction() {
    fun onCall() = calculateResult() * 2
}

fun onStartup() {
    var result = EPS.call("EchoAction")
    EPS.setVariable("result", result)
    EPS.logger.debug("Action returned: {}", result)
}
