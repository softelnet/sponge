"""
Sponge Knowledge base
Demo Forms - Base
"""

from java.lang import System

class ChangedButtonLabelsForm(Action):
    def onConfigure(self):
        self.withLabel("Changed button labels form")
        self.withArgs([
            ArgMeta("version", StringType().withNullable()).withLabel("Sponge version").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
        self.withFeatures({"callLabel":"Call", "refreshLabel":"Reload", "clearLabel":"Reset", "cancelLabel":"Close"})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, context):
        if "version" in context.names:
            context.provided["version"] = ArgProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

class HiddenButtonsForm(Action):
    def onConfigure(self):
        self.withLabel("Hidden buttons form")
        self.withArgs([
            ArgMeta("version", StringType().withNullable()).withLabel("Sponge version").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
        self.withFeatures({"callLabel":"Call", "refreshLabel":None, "clearLabel":None, "cancelLabel":None})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, context):
        if "version" in context.names:
            context.provided["version"] = ArgProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

class DefaultCallButtonForm(Action):
    def onConfigure(self):
        self.withLabel("Default label for the call button form")
        self.withArgs([
            ArgMeta("version", StringType().withNullable()).withLabel("Sponge version").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
        self.withFeatures({"refreshLabel":None, "clearLabel":None, "cancelLabel":None})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, context):
        if "version" in context.names:
            context.provided["version"] = ArgProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))
