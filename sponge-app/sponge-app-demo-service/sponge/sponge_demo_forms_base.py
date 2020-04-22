"""
Sponge Knowledge base
Demo Forms - Base
"""

from java.lang import System

class ChangedButtonLabelsForm(Action):
    def onConfigure(self):
        self.withLabel("Changed button labels form")
        self.withArgs([
            StringType("version").withNullable().withLabel("Sponge version").withProvided(ProvidedMeta().withValue().withReadOnly()),
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(StringType().withLabel("Upper case text"))
        self.withFeatures({"callLabel":"Call", "refreshLabel":"Reload", "clearLabel":"Reset", "cancelLabel":"Close"})
        self.withFeature("icon", "file-document")
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, context):
        if "version" in context.provide:
            context.provided["version"] = ProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

class HiddenButtonsForm(Action):
    def onConfigure(self):
        self.withLabel("Hidden buttons form")
        self.withArgs([
            StringType("version").withNullable().withLabel("Sponge version").withProvided(ProvidedMeta().withValue().withReadOnly()),
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(StringType().withLabel("Upper case text"))
        self.withFeatures({"callLabel":"Call", "icon":"file-document"})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, context):
        if "version" in context.provide:
            context.provided["version"] = ProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

class DefaultCallButtonForm(Action):
    def onConfigure(self):
        self.withLabel("Default label for the call button form")
        self.withArgs([
            StringType("version").withNullable().withLabel("Sponge version").withProvided(ProvidedMeta().withValue().withReadOnly()),
            StringType("text").withMaxLength(256).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(StringType().withLabel("Upper case text"))
        self.withFeatures({"icon":"file-document"})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, context):
        if "version" in context.provide:
            context.provided["version"] = ProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))
