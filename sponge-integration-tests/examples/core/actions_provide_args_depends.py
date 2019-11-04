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
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet()),
            BooleanType("actuator2").withLabel("Actuator 2 state").withProvided(ProvidedMeta().withValue()),
            IntegerType("actuator3").withNullable().withLabel("Actuator 3 state").withProvided(ProvidedMeta().withValue().withReadOnly()),
            IntegerType("actuator4").withLabel("Actuator 4 state"),
            StringType("actuator5").withLabel("Actuator 5 state").withProvided(ProvidedMeta().withValue().withValueSet().withDependency("actuator1"))
        ]).withNoResult()
    def onCall(self, actuator1, actuator2, actuator3, actuator4, actuator5):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        # actuator3 is read only in this action.
        sponge.setVariable("actuator4", actuator4)
        sponge.setVariable("actuator5", actuator5)
    def onProvideArgs(self, context):
        if "actuator1" in context.provide:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withAnnotatedValueSet(
                [AnnotatedValue("A").withValueLabel("Value A"), AnnotatedValue("B").withValueLabel("Value B"), AnnotatedValue("C").withValueLabel("Value C")])
        if "actuator2" in context.provide:
            context.provided["actuator2"] = ProvidedValue().withValue(sponge.getVariable("actuator2", None))
        if "actuator3" in context.provide:
            context.provided["actuator3"] = ProvidedValue().withValue(sponge.getVariable("actuator3", None))
        if "actuator5" in context.provide:
            context.provided["actuator5"] = ProvidedValue().withValue(sponge.getVariable("actuator5", None)).withValueSet([
                "X", "Y", "Z", context.current["actuator1"]])

def onStartup():
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator", ["actuator1"])["actuator1"].getValue())
