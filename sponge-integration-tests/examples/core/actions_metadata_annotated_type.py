"""
Sponge Knowledge base
Action metadata annotated type
"""

class AnnotatedTypeAction(Action):
    def onConfigure(self):
        self.withNoArgs().withResult(StringType().withAnnotated().withLabel("Annotated result"))
    def onCall(self):
        return AnnotatedValue("RESULT").withFeatures({"feature1":"value1", "feature2":"value2"})
