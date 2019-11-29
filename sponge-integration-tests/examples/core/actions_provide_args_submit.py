"""
Sponge Knowledge base
Provide action arguments
"""

def onInit():
    sponge.setVariable("actuator1", "A")
    sponge.setVariable("actuator2", False)

class SetActuator(Action):
    def onConfigure(self):
        self.withLabel("Set actuator").withDescription("Sets the actuator state.")
        self.withArgs([
            StringType("actuator1").withLabel("Actuator 1 state").withProvided(ProvidedMeta().withValue().withValueSet().withSubmittable()),
            BooleanType("actuator2").withLabel("Actuator 2 state").withProvided(ProvidedMeta().withValue())
        ]).withNoResult()
    def onCall(self, actuator1, actuator2):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
    def onProvideArgs(self, context):
        if "actuator1" in context.submit:
            # Set an actuator value with submit.
            sponge.setVariable("actuator1", context.current["actuator1"])

        if "actuator1" in context.provide:
            context.provided["actuator1"] = ProvidedValue().withValue(sponge.getVariable("actuator1", None)).withValueSet(["A", "B", "C"])
        if "actuator2" in context.provide:
            context.provided["actuator2"] = ProvidedValue().withValue(sponge.getVariable("actuator2", None))

def onStartup():
    sponge.provideActionArgs("SetActuator", ProvideArgsParameters().withSubmit(["actuator1"]).withCurrent({"actuator1":"B"}))
    sponge.logger.debug("The provided value of actuator1 is: {}", sponge.provideActionArgs("SetActuator",
                    ProvideArgsParameters().withProvide(["actuator1"]))["actuator1"].getValue())

    # Reset the state for tests.
    onInit()