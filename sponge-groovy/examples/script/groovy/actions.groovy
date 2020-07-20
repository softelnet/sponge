/**
 * Sponge Knowledge Base
 * Defining, calling and disabling Actions
 */

import org.openksavi.sponge.examples.PowerEchoAction

void onInit() {
    // Variables for assertions only
    sponge.setVariable("scriptActionResult", null)
    sponge.setVariable("javaActionResult", null)
}

class EchoAction extends Action {
    void onConfigure() {
        this.withLabel("Echo Action")
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
    sponge.enableJava(PowerEchoAction)
}

void onStartup() {
    sponge.logger.debug("Calling script defined action")
    def scriptActionResult = sponge.call("EchoAction", [1, "test"])
    sponge.setVariable("scriptActionResult", scriptActionResult)
    sponge.logger.debug("Action returned: {}", scriptActionResult)

    sponge.logger.debug("Calling Java defined action")
    def javaActionResult = sponge.call("PowerEchoAction", [1, "test"])
    sponge.setVariable("javaActionResult", javaActionResult)
    sponge.logger.debug("Action returned: {}", javaActionResult)

    sponge.logger.debug("Disabling actions")
    sponge.disable(EchoAction)
    sponge.disableJava(PowerEchoAction)
}
