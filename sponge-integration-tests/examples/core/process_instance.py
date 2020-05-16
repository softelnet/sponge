"""
Sponge Knowledge base
External process
"""

class ProcessEcho(Action):
    def onCall(self):
        process = sponge.process("echo", "TEST").outputAsString().run()
        return process.outputString

class ProcessEnv(Action):
    def onCall(self):
        process = sponge.process("printenv", "TEST_VARIABLE").env("TEST_VARIABLE", "TEST").outputAsString().run()
        return process.outputString

class ProcessWaitForOutput(Action):
    def onCall(self):
        process = sponge.process("echo", "MSG").outputAsConsumer().waitForPositiveLineRegexp(".*MSG.*").waitForNegativeLineRegexp(".*ERROR.*").run()
        return "OK"
