"""
Sponge Knowledge base
Demo - Action - a annotated arg with a default value
"""

class AnnotatedWithDefaultValue(Action):
    def onConfigure(self):
        self.withLabel("Annotated with default").withArgs([
            StringType("annotated").withLabel("Annotated").withAnnotated().withDefaultValue("Value"),
        ]).withResult(StringType())
    def onCall(self, annotated):
        return annotated.value
