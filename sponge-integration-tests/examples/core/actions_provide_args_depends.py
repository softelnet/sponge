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
        self.withLabel("Set actuator").withDescription("Sets the actuator state.")
        self.withArgs([
            ArgMeta("actuator1", StringType()).withLabel("Actuator 1 state").withProvided(ArgProvidedMeta().withValue().withValueSet()),
            ArgMeta("actuator2", BooleanType()).withLabel("Actuator 2 state").withProvided(ArgProvidedMeta().withValue()),
            ArgMeta("actuator3", IntegerType().withNullable()).withLabel("Actuator 3 state").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("actuator4", IntegerType()).withLabel("Actuator 4 state"),
            ArgMeta("actuator5", StringType()).withLabel("Actuator 5 state").withProvided(ArgProvidedMeta().withValue().withValueSet().withDepends("actuator1"))
        ]).withNoResult()
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
