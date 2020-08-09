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

    def onProvideArgs(self, context):
        if "arg1" in context.provide:
            context.provided["arg1"] = ProvidedValue().withValue("a")

        # Current arg1 is set when it is submitted or any of arg2, arg3 or arg4 is to be provided.
        arg1 = context.current.get("arg1")

        if "arg2" in context.provide or "arg1" in context.submit:
            context.provided["arg2"] = ProvidedValue().withValue(arg1.upper() if arg1 else None)
        if "arg3" in context.provide or "arg1" in context.submit:
            context.provided["arg3"] = ProvidedValue().withValue(arg1.upper() if arg1 else None)
        if "arg4" in context.provide or "arg1" in context.submit:
            context.provided["arg4"] = ProvidedValue().withValue(arg1.upper() if arg1 else None)
