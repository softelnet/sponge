"""
Sponge Knowledge base
Defining, calling and disabling Actions
"""

from org.openksavi.sponge.examples import PowerEchoAction

def onInit():
    # Variables for assertions only
    EPS.setVariable("scriptActionResult", None)
    EPS.setVariable("javaActionResult", None)

class EchoAction(Action):
    def onConfigure(self):
        self.displayName = "Echo Action"
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
        if (args is not None):
            for arg in args:
                self.logger.debug("Arg: {} ({})", arg, type(arg))
        else:
            self.logger.debug("No arguments supplied.")
    	return args

def onLoad():
    EPS.enableJava(PowerEchoAction)

def onStartup():
    EPS.logger.debug("Calling script defined action")
    scriptActionResult = EPS.call("EchoAction", 1, "test")
    EPS.logger.debug("Action returned: {}", scriptActionResult)
    EPS.setVariable("scriptActionResult", scriptActionResult)

    EPS.logger.debug("Calling Java defined action")
    javaActionResult = EPS.call("PowerEchoAction", 1, "test")
    EPS.logger.debug("Action returned: {}", javaActionResult)
    EPS.setVariable("javaActionResult", javaActionResult)

    EPS.logger.debug("Disabling actions")
    EPS.disable(EchoAction)
    EPS.disableJava(PowerEchoAction)
