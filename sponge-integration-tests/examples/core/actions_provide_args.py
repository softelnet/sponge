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
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet()),
            BooleanType("actuator2").withLabel("Actuator 2 state").withProvided(ProvidedMeta().withValue()),
            IntegerType("actuator3").withNullable().withLabel("Actuator 3 state").withProvided(ProvidedMeta().withValue().withReadOnly()),
            IntegerType("actuator4").withLabel("Actuator 4 state")
        ]).withNoResult()
    def onCall(self, actuator1, actuator2, actuator3, actuator4):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
    def onProvideArgs(self, context):
        if "actuator1" in context.names:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])
        if "actuator2" in context.names:
            context.provided["actuator2"] = ProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in context.names:
            context.provided["actuator3"] = ProvidedValue().withValue(sponge.getVariable("actuator3", None))

class SetActuatorAnnotatedValueSet(Action):
    def onConfigure(self):
        self.withLabel("Set actuator type").withDescription("Sets the actuator type.")
        self.withArg(
            StringType("actuatorType").withLabel("Actuator type").withProvided(ProvidedMeta().withValue().withValueSet())
        ).withNoResult()
    def onCall(self, actuatorType):
        sponge.setVariable("actuatorType", actuatorType)
    def onProvideArgs(self, context):
        if "actuatorType" in context.names:
            context.provided["actuatorType"] = ProvidedValue().withValue(sponge.getVariable("actuatorType", None)).withAnnotatedValueSet(
                [AnnotatedValue("auto").withLabel("Auto"), AnnotatedValue("manual").withLabel("Manual")])

class SetActuatorNotLimitedValueSet(Action):
    def onConfigure(self):
        self.withLabel("Set actuator not limited value set")
        self.withArgs([
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet(ValueSetMeta().withNotLimited())),
        ]).withNoResult()
    def onCall(self, actuator1):
        pass
    def onProvideArgs(self, context):
        if "actuator1" in context.names:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])

class ProvidedArgNoCallAction(Action):
    def onConfigure(self):
        self.withArgs([
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet(ValueSetMeta().withNotLimited())),
        ]).withNoResult().withCallable(False)
    def onProvideArgs(self, context):
        if "actuator1" in context.names:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])

def onStartup():
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator")["actuator1"].getValue())
