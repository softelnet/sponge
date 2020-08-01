"""
Sponge Knowledge Base
Demo
"""

from java.util import Random

class SubmittableActionArg(Action):
    def onConfigure(self):
        self.withLabel("Submittable argument")
        self.withArgs([
            StringType("arg1").withLabel("Argument 1").withNullable().withProvided(
                ProvidedMeta().withValue().withSubmittable(
                    SubmittableMeta().withInfluences(["arg2", "arg3", "arg4"]))).withFeatures({"responsive":True}),
            StringType("arg2").withLabel("Argument 2").withNullable().withReadOnly().withProvided(
                ProvidedMeta().withValue().withDependency("arg1").withLazyUpdate().withOptionalMode()),
            StringType("arg3").withLabel("Argument 3").withNullable().withReadOnly().withProvided(
                ProvidedMeta().withValue().withDependency("arg1").withOptionalMode()),
            StringType("arg4").withLabel("Argument 4").withReadOnly().withProvided(
                ProvidedMeta().withValue().withDependency("arg1").withOptionalMode()),
        ]).withNonCallable()
        self.withFeatures({"cancelLabel":"Close"})
    def onInit(self):
        self.arg1value = "a"
    def onProvideArgs(self, context):
        if "arg1" in context.submit:
            self.arg1value = context.current["arg1"]

        if "arg1" in context.provide:
            context.provided["arg1"] = ProvidedValue().withValue(self.arg1value)
        if "arg2" in context.provide or "arg1" in context.submit:
            context.provided["arg2"] = ProvidedValue().withValue(self.arg1value.upper() if self.arg1value else None)
        if "arg3" in context.provide or "arg1" in context.submit:
            context.provided["arg3"] = ProvidedValue().withValue(self.arg1value.upper() if self.arg1value else None)
        if "arg4" in context.provide or "arg1" in context.submit:
            context.provided["arg4"] = ProvidedValue().withValue(self.arg1value.upper() if self.arg1value else None)
