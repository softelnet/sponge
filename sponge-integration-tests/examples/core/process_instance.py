"""
Sponge Knowledge base
External process
"""

from org.openksavi.sponge.util.process import ProcessConfiguration, OutputRedirect

class ProcessEcho(Action):
    def onCall(self):
        process = sponge.runProcess(ProcessConfiguration.builder("echo").arguments("TEST").outputRedirect(OutputRedirect.STRING).build())
        return process.outputString

class ProcessEnv(Action):
    def onCall(self):
        process = sponge.runProcess(ProcessConfiguration.builder("printenv").arguments("TEST_VARIABLE")
                .env("TEST_VARIABLE", "TEST").outputRedirect(OutputRedirect.STRING).build())
        return process.outputString

class ProcessWaitForOutput(Action):
    def onCall(self):
        process = sponge.runProcess(ProcessConfiguration.builder("echo").arguments("MSG").outputRedirect(OutputRedirect.LOGGER)
                .waitForPositiveLineRegexp(".*MSG.*").waitForNegativeLineRegexp(".*ERROR.*").build())
        return "OK"
