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
    Object onCall(Object[] args) {
        this.logger.info("Action {} called", this.name)
        if (args != null) {
            for (arg in args) {
                this.logger.debug("Arg: {} ({})", arg, arg.class)
            }
        } else {
            this.logger.debug("No arguments supplied.")
        }
    	return args
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
    def javaActionResult = EPS.call("org.openksavi.sponge.examples.PowerEchoAction", 1, "test")
    EPS.setVariable("javaActionResult", javaActionResult)
    EPS.logger.debug("Action returned: {}", javaActionResult)

    EPS.logger.debug("Disabling actions")
    EPS.disable(EchoAction)
    EPS.disableJava(PowerEchoAction)
}
