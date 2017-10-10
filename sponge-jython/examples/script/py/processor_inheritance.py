"""
Sponge Knowledge base
Processor inheritance
"""

def onInit():
    # Variables for assertions only
    EPS.setVariable("result", None)

class AbstractEchoAction(Action):
    def calculateResult(self, args):
        return 1

class EchoAction(AbstractEchoAction):
    def onCall(self, args):
        return self.calculateResult(args) * 2

def onStartup():
    result = EPS.call("EchoAction")
    EPS.setVariable("result", result)
    EPS.logger.debug("Action returned: {}", result)
