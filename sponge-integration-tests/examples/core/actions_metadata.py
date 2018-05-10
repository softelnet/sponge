"""
Sponge Knowledge base
Action argument definitions
"""

from org.openksavi.sponge.examples import PowerEchoMetadataAction

def onInit():
    # Variables for assertions only
    EPS.setVariable("scriptActionResult", None)
    EPS.setVariable("javaActionResult", None)

class UpperEchoAction(Action):
    def onConfigure(self):
        self.displayName = "Echo Action"
        self.argsMetadata = [ "arg1:number", "arg2:string" ]
    def onCall(self, args):
        self.logger.info("Action {} called", self.name)
    	return [str(s).upper() for s in args]

def onLoad():
    EPS.enableJava(PowerEchoMetadataAction)

def onStartup():
    EPS.logger.debug("Calling script defined action")
    scriptActionResult = EPS.call("UpperEchoAction", 1, "test")
    EPS.logger.debug("Action returned: {}", scriptActionResult)
    EPS.setVariable("scriptActionResult", scriptActionResult)

    EPS.logger.debug("Calling Java defined action")
    javaActionResult = EPS.call("PowerEchoMetadataAction", 1, "test")
    EPS.logger.debug("Action returned: {}", javaActionResult)
    EPS.setVariable("javaActionResult", javaActionResult)

