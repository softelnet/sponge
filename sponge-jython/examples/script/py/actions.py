"""
Sponge Knowledge base
Defining, calling and disabling Actions
"""

from org.openksavi.sponge.examples import PowerEchoAction

def onInit():
    # Variables for assertions only
    sponge.setVariable("scriptActionResult", None)
    sponge.setVariable("javaActionResult", None)

class EchoAction(Action):
    def onConfigure(self):
        self.displayName = "Echo Action"
    def onCall(self, value, text):
    	return [value, text]

class ArrayArgumentAction(Action):
    def onCall(self, arrayArg):
        return len(arrayArg)

def onLoad():
    sponge.enableJava(PowerEchoAction)

def onStartup():
    sponge.logger.debug("Calling script defined action")
    scriptActionResult = sponge.call("EchoAction", [1, "test"])
    sponge.logger.debug("Action returned: {}", scriptActionResult)
    sponge.setVariable("scriptActionResult", scriptActionResult)

    sponge.logger.debug("Calling Java defined action")
    javaActionResult = sponge.call("PowerEchoAction", [1, "test"])
    sponge.logger.debug("Action returned: {}", javaActionResult)
    sponge.setVariable("javaActionResult", javaActionResult)

    sponge.logger.debug("Disabling actions")
    sponge.disable(EchoAction)
    sponge.disableJava(PowerEchoAction)
