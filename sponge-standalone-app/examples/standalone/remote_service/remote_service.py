"""
Sponge Knowledge Base
Remote service
"""

class HelloWorld(Action):
    def onConfigure(self):
        self.withLabel("Hello world").withDescription("Returns a greeting text.")
        self.withArg(StringType("name").withLabel("Your name").withDescription("Type your name."))
        self.withResult(StringType().withLabel("Greeting").withDescription("The greeting text."))
    def onCall(self, name):
        return "Hello World! Hello {}!".format(name)