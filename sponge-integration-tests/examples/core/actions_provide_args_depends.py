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
        self.label = "Set actuator"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).label("Actuator 1 state").provided(ArgProvidedMeta().value().valueSet()),
            ArgMeta("actuator2", BooleanType()).label("Actuator 2 state").provided(ArgProvidedMeta().value()),
            ArgMeta("actuator3", IntegerType().nullable()).label("Actuator 3 state").provided(ArgProvidedMeta().value().readOnly()),
            ArgMeta("actuator4", IntegerType()).label("Actuator 4 state"),
            ArgMeta("actuator5", StringType()).label("Actuator 5 state").provided(ArgProvidedMeta().value().valueSet().depends("actuator1")),
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4, actuator5):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
        sponge.setVariable("actuator5", actuator5)
    def onProvideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgProvidedValue().withValue(sponge.getVariable("actuator1", None)).withAnnotatedValueSet(
                [AnnotatedValue("A").withLabel("Value A"), AnnotatedValue("B").withLabel("Value B"), AnnotatedValue("C").withLabel("Value C")])
        if "actuator2" in names:
            provided["actuator2"] = ArgProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in names:
            provided["actuator3"] = ArgProvidedValue().withValue(sponge.getVariable("actuator3", None))
        if "actuator5" in names:
            provided["actuator5"] = ArgProvidedValue().withValue(sponge.getVariable("actuator5", None)).withValueSet(["X", "Y", "Z", current["actuator1"]])

def onStartup():
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator", ["actuator1"])["actuator1"].getValue())
