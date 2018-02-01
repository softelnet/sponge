/*
 * Sponge Knowledge base
 * Processor inheritance
 */

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("result", null)
}

abstract class AbstractEchoAction : Action() {
    open fun calculateResult(vararg args: Any?) = 1
}

class EchoAction : AbstractEchoAction() {
    override fun onCall(vararg args: Any?) = calculateResult(args) * 2
}

fun onStartup() {
    var result = EPS.call("EchoAction")
    EPS.setVariable("result", result)
    EPS.logger.debug("Action returned: {}", result)
}
