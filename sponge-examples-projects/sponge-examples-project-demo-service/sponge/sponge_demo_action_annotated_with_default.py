"""
Sponge Knowledge base
Demo - Action - a annotated arg with a default value
"""

class AnnotatedWithDefaultValue(Action):
    def onConfigure(self):
        self.withLabel("Action with annotated arg with default").withArgs([
            StringType("annotated").withLabel("Annotated").withAnnotated().withDefaultValue(AnnotatedValue("Value"))
        ]).withResult(StringType())
    def onCall(self, annotated):
        return annotated.value
