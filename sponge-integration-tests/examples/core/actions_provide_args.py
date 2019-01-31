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
        self.label = "Set actuator"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).label("Actuator 1 state").provided(ArgProvided().value().valueSet()),
            ArgMeta("actuator2", BooleanType()).label("Actuator 2 state").provided(ArgProvided().value()),
            ArgMeta("actuator3", IntegerType().nullable()).label("Actuator 3 state").provided(ArgProvided().value().readOnly()),
            ArgMeta("actuator4", IntegerType()).label("Actuator 4 state")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
    def onProvideArgs(self, names, current, provided):
        if "actuator1" in names:
            provided["actuator1"] = ArgValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])
        if "actuator2" in names:
            provided["actuator2"] = ArgValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in names:
            provided["actuator3"] = ArgValue().withValue(sponge.getVariable("actuator3", None))

class SetActuatorAnnotatedValueSet(Action):
    def onConfigure(self):
        self.label = "Set actuator type"
        self.description = "Sets the actuator type."
        self.argsMeta = [
            ArgMeta("actuatorType", StringType()).label("Actuator type").provided(ArgProvided().value().valueSet()),
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuatorType):
        sponge.setVariable("actuatorType", actuatorType)
    def onProvideArgs(self, names, current, provided):
        if "actuatorType" in names:
            provided["actuatorType"] = ArgValue().withValue(sponge.getVariable("actuatorType", None)).withAnnotatedValueSet(
                [AnnotatedValue("auto").withLabel("Auto"), AnnotatedValue("manual").withLabel("Manual")])

def onStartup():
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator")["actuator1"].getValue())
