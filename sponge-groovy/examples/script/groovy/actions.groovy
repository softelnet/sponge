/**
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

import org.openksavi.sponge.examples.PowerEchoAction

void onInit() {
    // Variables for assertions only
    EPS.setVariable("scriptActionResult", null)
    EPS.setVariable("javaActionResult", null)
}

class EchoAction extends Action {
    void onConfigure() {
        this.displayName = "Echo Action"
    }

    Object onCall(int value, String text) {
        return [value, text]
    }
}

class ArrayArgumentAction extends Action {
    Object onCall(arrayArg) {
        return arrayArg.length
    }
}

void onLoad() {
    EPS.enableJava(PowerEchoAction)
}

void onStartup() {
    EPS.logger.debug("Calling script defined action")
    def scriptActionResult = EPS.call("EchoAction", 1, "test")
    EPS.setVariable("scriptActionResult", scriptActionResult)
    EPS.logger.debug("Action returned: {}", scriptActionResult)

    EPS.logger.debug("Calling Java defined action")
    def javaActionResult = EPS.call("PowerEchoAction", 1, "test")
    EPS.setVariable("javaActionResult", javaActionResult)
    EPS.logger.debug("Action returned: {}", javaActionResult)

    EPS.logger.debug("Disabling actions")
    EPS.disable(EchoAction)
    EPS.disableJava(PowerEchoAction)
}
