"""
Sponge Knowledge base
Provide action arguments
"""

def onInit():
    sponge.setVariable("actuator1", "A")
    sponge.setVariable("actuator2", False)
    sponge.setVariable("actuator3", 1)
    sponge.setVariable("actuator4", 1)
    sponge.setVariable("actuator5", "X")

class SetActuator(Action):
    def onConfigure(self):
        self.displayName = "Set actuator"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).displayName("Actuator 1 state").provided(),
            ArgMeta("actuator2", BooleanType()).displayName("Actuator 2 state").provided(),
            ArgMeta("actuator3", IntegerType().nullable()).displayName("Actuator 3 state").provided().readOnly(),
            ArgMeta("actuator4", IntegerType()).displayName("Actuator 4 state"),
            ArgMeta("actuator5", StringType()).displayName("Actuator 5 state").provided().depends("actuator1"),
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4, actuator5):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
        sponge.setVariable("actuator5", actuator5)
    def provideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgValue().value(sponge.getVariable("actuator1", None)).valueSet(["A", "B", "C"])
        if "actuator2" in names:
            provided["actuator2"] = ArgValue().value(sponge.getVariable("actuator2", None))
        if "actuator3" in names:
            provided["actuator3"] = ArgValue().value(sponge.getVariable("actuator3", None))
        if "actuator4" in names:
            provided["actuator4"] = ArgValue().valueSet([2, 4, 8])
        if "actuator5" in names:
            provided["actuator5"] = ArgValue().value(sponge.getVariable("actuator5", None)).valueSet(["X", "Y", "Z", current["actuator1"]])

def onStartup():
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator", ["actuator1"])["actuator1"].getValue())