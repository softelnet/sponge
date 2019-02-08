"""
Sponge Knowledge base
Action argument definitions
"""

from org.openksavi.sponge.examples import PowerEchoMetadataAction

def onInit():
    # Variables for assertions only
    sponge.setVariable("scriptActionResult", None)
    sponge.setVariable("javaActionResult", None)

class UpperEchoAction(Action):
    def onConfigure(self):
        self.withLabel("Echo Action").withDescription("Returns the upper case string")
        self.withArg(ArgMeta("text", StringType()).withLabel("Argument 1").withDescription("Argument 1 description"))
        self.withResult(ResultMeta(StringType()).withLabel("Upper case string").withDescription("Result description"))
    def onCall(self, text):
        self.logger.info("Action {} called", self.meta.name)
    	return text.upper()

def onLoad():
    sponge.enableJava(PowerEchoMetadataAction)

def onStartup():
    sponge.logger.debug("Calling script defined action")
    scriptActionResult = sponge.call("UpperEchoAction", ["test"])
    sponge.logger.debug("Action returned: {}", scriptActionResult)
    sponge.setVariable("scriptActionResult", scriptActionResult)

    sponge.logger.debug("Calling Java defined action")
    javaActionResult = sponge.call("PowerEchoMetadataAction", [1, "test"])
    sponge.logger.debug("Action returned: {}", javaActionResult)
    sponge.setVariable("javaActionResult", javaActionResult)

