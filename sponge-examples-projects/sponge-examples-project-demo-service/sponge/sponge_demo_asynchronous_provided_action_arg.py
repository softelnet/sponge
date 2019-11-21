"""
Sponge Knowledge base
Demo
"""

from java.util import Random

class AsynchronousProvidedActionArg(Action):
    def onConfigure(self):
        self.withLabel("Asynchronous provided argument")
        self.withArgs([
            StringType("arg1").withLabel("Argument 1").withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue().withReadOnly()),
            StringType("arg2").withLabel("Argument 2").withProvided(
                ProvidedMeta().withValue().withReadOnly().withDependency("arg1")),
        ]).withNoResult().withCallable(False).withFeatures({"cancelLabel":"Close"})
    def onProvideArgs(self, context):
        if "arg1" in context.provide:
            context.provided["arg1"] = ProvidedValue().withValue("v" + str(Random().nextInt(100) + 1))
        if "arg2" in context.provide:
            TimeUnit.SECONDS.sleep(5)
            context.provided["arg2"] = ProvidedValue().withValue("First arg is " + context.current["arg1"])