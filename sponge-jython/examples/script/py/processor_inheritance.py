"""
Sponge Knowledge base
Processor inheritance
"""

def onInit():
    # Variables for assertions only
    sponge.setVariable("result", None)

class AbstractEchoAction(Action):
    def calculateResult(self):
        return 1

class EchoAction(AbstractEchoAction):
    def onCall(self):
        return self.calculateResult() * 2

def onStartup():
    result = sponge.call("EchoAction")
    sponge.setVariable("result", result)
    sponge.logger.debug("Action returned: {}", result)
