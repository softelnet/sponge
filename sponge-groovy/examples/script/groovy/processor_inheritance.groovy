/**
 * Sponge Knowledge base
 * Processor inheritance
 */

void onInit() {
    // Variables for assertions only
    EPS.setVariable("result", null)
}

abstract class AbstractEchoAction extends Action {
    Object calculateResult(Object[] args) {
        return 1
    }
}

class EchoAction extends AbstractEchoAction {
    Object onCall(Object[] args) {
        return calculateResult(args) * 2
    }
}

void onStartup() {
    def result = EPS.call("EchoAction")
    EPS.setVariable("result", result)
    EPS.logger.debug("Action returned: {}", result)
}
