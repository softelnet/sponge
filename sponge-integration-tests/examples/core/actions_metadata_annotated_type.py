"""
Sponge Knowledge base
Action metadata annotated type
"""

class AnnotatedTypeAction(Action):
    def onConfigure(self):
        self.argsMeta = []
        self.resultMeta = ResultMeta(AnnotatedType(StringType())).withLabel("Annotated result")
    def onCall(self):
        return AnnotatedValue("RESULT").withFeatures({"feature1":"value1", "feature2":"value2"})
