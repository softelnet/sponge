"""
Sponge Knowledge base
Demo - action context actions
"""

class ActionWithContextActions(Action):
    def onConfigure(self):
        self.withLabel("Action with context actions").withArgs([
            StringType("arg1").withLabel("Argument 1"),
            StringType("arg2").withLabel("Argument 2")
        ]).withNoResult().withFeature("contextActions", [
            "ActionWithContextActionsContextAction1", "ActionWithContextActionsContextAction2(arg2)", "ActionWithContextActionsContextAction3(arg2=arg2)",
            "ActionWithContextActionsContextAction4(arg1)", "ActionWithContextActionsContextAction5"
        ])
        self.withFeature("icon", "attachment")
    def onCall(self, arg1, arg2):
        pass

class ActionWithContextActionsContextAction1(Action):
    def onConfigure(self):
        self.withLabel("Context action 1").withArgs([
            RecordType("arg").withFields([
                StringType("arg1").withLabel("Argument 1"),
                StringType("arg2").withLabel("Argument 2")
            ])
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg):
        return arg["arg1"]

class ActionWithContextActionsContextAction2(Action):
    def onConfigure(self):
        self.withLabel("Context action 2").withArgs([
            StringType("arg").withLabel("Argument"),
            StringType("additionalText").withLabel("Additional text"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg, additionalText):
        return arg + " " + additionalText

class ActionWithContextActionsContextAction3(Action):
    def onConfigure(self):
        self.withLabel("Context action 3").withArgs([
            StringType("arg1").withLabel("Argument 1"),
            StringType("arg2").withLabel("Argument 2"),
            StringType("additionalText").withLabel("Additional text"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg1, arg2, additionalText):
        return arg1 + " " + arg2 + " " + additionalText

class ActionWithContextActionsContextAction4(Action):
    def onConfigure(self):
        self.withLabel("Context action 4").withArgs([
            StringType("arg1NotVisible").withLabel("Argument 1 not visible").withFeatures({"visible":False}),
            StringType("arg2").withLabel("Argument 2"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg1NotVisible, arg2):
        return arg1NotVisible + " " + arg2

class ActionWithContextActionsContextAction5(Action):
    def onConfigure(self):
        self.withLabel("Context action 5").withArgs([
            RecordType("arg").withFields([
                StringType("arg1").withLabel("Argument 1"),
                StringType("arg2").withLabel("Argument 2")
            ]).withFeatures({"visible":False}),
            StringType("additionalText").withLabel("Additional text")
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, arg, additionalText):
        return arg["arg1"] + " " + additionalText

