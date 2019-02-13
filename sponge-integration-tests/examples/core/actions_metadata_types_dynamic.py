"""
Sponge Knowledge base
Action metadata dynamic types
"""

class DynamicResultAction(Action):
    def onConfigure(self):
        self.withArg(ArgMeta("type", StringType())).withResult(ResultMeta(DynamicType()))
    def onCall(self, type):
        if type == "string":
            return DynamicValue("text", StringType())
        elif type == "boolean":
            return DynamicValue(True, BooleanType())
        else:
            return None

class TypeResultAction(Action):
    def onConfigure(self):
        self.withArg(ArgMeta("type", StringType())).withResult(ResultMeta(TypeType()))
    def onCall(self, type):
        if type == "string":
            return StringType()
        elif type == "boolean":
            return BooleanType()
        else:
            return None