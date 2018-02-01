/*
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

import org.openksavi.sponge.examples.PowerEchoAction

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("scriptActionResult", null)
    EPS.setVariable("javaActionResult", null)
}

class EchoAction : Action() {
    override fun onConfigure() {
        displayName = "Echo Action"
    }

    override fun onCall(vararg args: Any?): Any? {
        logger.info("Action {} called", name)
        args.forEach { logger.debug("Arg: $it (${it?.javaClass})") }
        return args
    }
}

fun onLoad() {
    EPS.enableJava(PowerEchoAction::class.java)
}

fun onStartup() {
    EPS.logger.debug("Calling script defined action")
    val scriptActionResult = EPS.call("EchoAction", 1, "test")
    EPS.logger.debug("Action returned: {}", scriptActionResult)
    EPS.setVariable("scriptActionResult", scriptActionResult)

    EPS.logger.debug("Calling Java defined action")
    val javaActionResult = EPS.call("PowerEchoAction", 1, "test")
    EPS.logger.debug("Action returned: {}", javaActionResult)
    EPS.setVariable("javaActionResult", javaActionResult)

    EPS.logger.debug("Disabling actions")
    EPS.disable(EchoAction::class)
    EPS.disableJava(PowerEchoAction::class.java)
}

