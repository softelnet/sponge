"""
Sponge Knowledge base
Provide action arguments
"""

def onInit():
    sponge.setVariable("actuator1", "A")
    sponge.setVariable("actuator2", False)
    sponge.setVariable("actuator3", 1)
    sponge.setVariable("actuator4", 1)
    sponge.setVariable("actuatorType", "auto")

class SetActuator(Action):
    def onConfigure(self):
        self.withLabel("Set actuator").withDescription("Sets the actuator state.")
        self.withArgs([
            ArgMeta("actuator1", StringType()).withLabel("Actuator 1 state").withProvided(ArgProvidedMeta().withValue().withValueSet()),
            ArgMeta("actuator2", BooleanType()).withLabel("Actuator 2 state").withProvided(ArgProvidedMeta().withValue()),
            ArgMeta("actuator3", IntegerType().withNullable()).withLabel("Actuator 3 state").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("actuator4", IntegerType()).withLabel("Actuator 4 state")
        ]).withNoResult()
    def onCall(self, actuator1, actuator2, actuator3, actuator4):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
    def onProvideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])
        if "actuator2" in names:
            provided["actuator2"] = ArgProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in names:
            provided["actuator3"] = ArgProvidedValue().withValue(sponge.getVariable("actuator3", None))

class SetActuatorAnnotatedValueSet(Action):
    def onConfigure(self):
        self.withLabel("Set actuator type").withDescription("Sets the actuator type.")
        self.withArg(
            ArgMeta("actuatorType", StringType()).withLabel("Actuator type").withProvided(ArgProvidedMeta().withValue().withValueSet())
        ).withNoResult()
    def onCall(self, actuatorType):
        sponge.setVariable("actuatorType", actuatorType)
    def onProvideArgs(self, names, current, provided):
        if "actuatorType" in names:
            provided["actuatorType"] = ArgProvidedValue().withValue(sponge.getVariable("actuatorType", None)).withAnnotatedValueSet(
                [AnnotatedValue("auto").withLabel("Auto"), AnnotatedValue("manual").withLabel("Manual")])

class SetActuatorNotLimitedValueSet(Action):
    def onConfigure(self):
        self.withLabel("Set actuator not limited value set")
        self.withArgs([
            ArgMeta("actuator1", StringType()).withLabel("Actuator 1 state").withProvided(ArgProvidedMeta().withValue().withValueSet(ValueSetMeta().withNotLimited())),
        ]).withNoResult()
    def onCall(self, actuator1):
        pass
    def onProvideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])

def onStartup():
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator")["actuator1"].getValue())
