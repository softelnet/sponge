/**
 * Sponge Knowledge base
 * Processor inheritance
 */

void onInit() {
    // Variables for assertions only
    sponge.setVariable("result", null)
}

abstract class AbstractEchoAction extends Action {
    Object calculateResult() {
        return 1
    }
}

class EchoAction extends AbstractEchoAction {
    Object onCall() {
        return calculateResult() * 2
    }
}

void onStartup() {
    def result = sponge.call("EchoAction")
    sponge.setVariable("result", result)
    sponge.logger.debug("Action returned: {}", result)
}
