/*
 * Sponge Knowledge base
 * Processor inheritance
 */

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("result", null)
}

abstract class AbstractEchoAction : Action() {
    open fun calculateResult(args: Array<Any?>) = 1
}

class EchoAction : AbstractEchoAction() {
    override fun onCall(args: Array<Any?>) = calculateResult(args) * 2
}

fun onStartup() {
    var result = EPS.call("EchoAction")
    EPS.setVariable("result", result)
    EPS.logger.debug("Action returned: {}", result)
}
