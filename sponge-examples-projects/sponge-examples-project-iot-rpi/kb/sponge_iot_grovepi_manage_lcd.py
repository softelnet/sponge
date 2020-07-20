"""
Sponge Knowledge Base
GrovePi
"""

class ManageLcd(Action):
    def onConfigure(self):
        self.withLabel("Manage the LCD text and color")
        self.withDescription("Provides management of the LCD properties (display text and color). A null value doesn't change an LCD property.")
        self.withArgs([
            StringType("currentText").withMaxLength(256).withNullable(True).withReadOnly().withFeatures({"maxLines":2})
                .withLabel("Current LCD text").withDescription("The currently displayed LCD text.").withProvided(ProvidedMeta().withValue()),
            StringType("text").withMaxLength(256).withNullable(True).withFeatures({"maxLines":2})
                .withLabel("Text to display").withDescription("The text that will be displayed in the LCD.").withProvided(ProvidedMeta().withValue()),
            StringType("color").withMaxLength(6).withNullable(True).withFeatures({"characteristic":"color"})
                .withLabel("LCD color").withDescription("The LCD color.").withProvided(ProvidedMeta().withValue().withOverwrite()),
            BooleanType("clearText").withNullable(True).withDefaultValue(False)
                .withLabel("Clear text").withDescription("The text the LCD will be cleared.")
        ]).withNoResult()
        self.withFeatures({"icon":"monitor", "showRefresh":True, "refreshEvents":["lcdChange"]})
    def onCall(self, currentText, text, color, clearText = None):
        sponge.call("SetLcd", [text, color, clearText])
    def onProvideArgs(self, context):
        grovePiDevice = sponge.getVariable("grovePiDevice")
        if "currentText" in context.provide:
            context.provided["currentText"] = ProvidedValue().withValue(grovePiDevice.getLcdText())
        if "text" in context.provide:
            context.provided["text"] = ProvidedValue().withValue(grovePiDevice.getLcdText())
        if "color" in context.provide:
            context.provided["color"] = ProvidedValue().withValue(grovePiDevice.getLcdColor())

class SetLcd(Action):
    def onCall(self, text, color, clearText = None):
        sponge.getVariable("grovePiDevice").setLcd("" if (clearText or text is None) else text, color)
