"""
Sponge Knowledge base
External process
"""

from org.openksavi.sponge.util.process import ProcessConfiguration

class ProcessEcho(Action):
    def onCall(self):
        process = sponge.process(ProcessConfiguration.builder("echo", "TEST").outputAsString()).run()
        return process.outputString

class ProcessEnv(Action):
    def onCall(self):
        process = sponge.process(ProcessConfiguration.builder("printenv", "TEST_VARIABLE")
                .env("TEST_VARIABLE", "TEST").outputAsString()).run()
        return process.outputString

class ProcessWaitForOutput(Action):
    def onCall(self):
        process = sponge.process(ProcessConfiguration.builder("echo", "MSG").outputAsConsumer()
                .waitForPositiveLineRegexp(".*MSG.*").waitForNegativeLineRegexp(".*ERROR.*")).run()
        return "OK"
