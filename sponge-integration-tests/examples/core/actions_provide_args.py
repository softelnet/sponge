"""
Sponge Knowledge base
Provide action arguments
"""

def onInit():
    sponge.setVariable("actuator1", "A")
    sponge.setVariable("actuator2", False)
    sponge.setVariable("actuator3", 1)
    sponge.setVariable("actuator4", 1)

class SetActuator(Action):
    def onConfigure(self):
        self.displayName = "Set actuator"
        self.description = "Sets the actuator state."
        self.argsMeta = [
            ArgMeta("actuator1", StringType()).displayName("Actuator 1 state").provided(),
            ArgMeta("actuator2", BooleanType()).displayName("Actuator 2 state").provided(),
            ArgMeta("actuator3", IntegerType()).displayName("Actuator 3 state").provided(),
            ArgMeta("actuator4", IntegerType()).displayName("Actuator 4 state")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, actuator1, actuator2, actuator3, actuator4):
        sponge.setVariable("actuator1", actuator1)
        sponge.setVariable("actuator2", actuator2)
        sponge.setVariable("actuator3", actuator3)
        sponge.setVariable("actuator4", actuator4)
    def provideArgs(self, names, current):
        """ Arguments don't depend on each other, so they are returned all at once.
        """
        return {
            "actuator1":ArgValue().value(sponge.getVariable("actuator1", None)).valueSet(["A", "B", "C"]),
            "actuator2":ArgValue().value(sponge.getVariable("actuator2", None)),
            "actuator3":ArgValue().value(sponge.getVariable("actuator3", None))
        }
