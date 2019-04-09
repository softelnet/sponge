"""
Sponge Knowledge base
Demo
"""

class ChooseColor(Action):
    def onConfigure(self):
        self.withLabel("Choose a color").withDescription("Shows a color argument.")
        self.withArg(
            StringType("color").withMaxLength(6).withNullable(True).withFeatures({"characteristic":"color"})
                .withLabel("Color").withDescription("The color.")
        ).withResult(StringType())
        self.withFeatures({"icon":"format-color-fill"})
    def onCall(self, color):
        return ("The chosen color is " + color) if color else "No color chosen"
