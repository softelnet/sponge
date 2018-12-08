"""
Sponge Knowledge base
Provide action arguments
"""

def onInit():
    sponge.setVariable("actuator1", "X")
    sponge.setVariable("actuator2", "A")
    sponge.setVariable("actuator3", False)
    sponge.setVariable("actuator4", 1)
    sponge.setVariable("actuator5", 1)

class SetActuator(Action):
    def onConfigure(self):
        self.displayName = "Set actuator"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).displayName("Actuator 1 state").provided(),
            ArgMeta("actuator2", StringType()).displayName("Actuator 2 state").provided().depends("actuator1"),
            ArgMeta("actuator3", BooleanType()).displayName("Actuator 3 state").provided(),
            ArgMeta("actuator4", IntegerType()).displayName("Actuator 4 state").provided(),
            ArgMeta("actuator5", IntegerType()).displayName("Actuator 5 state")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4, actuator5):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        sponge.setVariable("actuator3", actuator3)
        sponge.setVariable("actuator4", actuator4)
        sponge.setVariable("actuator5", actuator5)
    def provideArgs(self, names, current):
        """ Arguments depend on each other, so they are returned selectively.
        """
        provided = {}
        if "actuator1" in names:
            provided["actuator1"] = ArgValue().value(sponge.getVariable("actuator1", None)).valueSet(["X", "Y", "Z"])
        if "actuator2" in names:
            provided["actuator2"] = ArgValue().value(sponge.getVariable("actuator2", None)).valueSet(["A", "B", "C", current["actuator1"]])
        if "actuator3" in names:
            provided["actuator3"] = ArgValue().value(sponge.getVariable("actuator3", None))
        if "actuator4" in names:
            provided["actuator4"] = ArgValue().valueSet(["a", "b", "c"])
        return provided
