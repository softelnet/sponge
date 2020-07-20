"""
Sponge Knowledge Base
Demo
"""

from java.util import Random

class AsynchronousProvidedActionArg(Action):
    def onConfigure(self):
        self.withLabel("Asynchronous provided argument")
        self.withArgs([
            StringType("arg1").withLabel("Argument 1").withFeatures({"multiline":True, "maxLines":2}).withReadOnly().withProvided(
                ProvidedMeta().withValue().withOverwrite()),
            StringType("arg2").withLabel("Argument 2").withReadOnly().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("arg1")),
        ]).withNonCallable().withFeatures({"cancelLabel":"Close"})
    def onProvideArgs(self, context):
        if "arg1" in context.provide:
            context.provided["arg1"] = ProvidedValue().withValue("v" + str(Random().nextInt(100) + 1))
        if "arg2" in context.provide:
            TimeUnit.SECONDS.sleep(5)
            context.provided["arg2"] = ProvidedValue().withValue("First arg is " + context.current["arg1"])