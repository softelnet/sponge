"""
Sponge Knowledge base
Provide action arguments - element value set
"""

class FruitsElementValueSetAction(Action):
    def onConfigure(self):
        self.withLabel("Fruits action with argument element value set")
        self.withArg(ListType("fruits", StringType()).withLabel("Fruits").withProvided(ProvidedMeta().withElementValueSet())).withResult(IntegerType())
    def onCall(self, fruits):
        return len(fruits)
    def onProvideArgs(self, context):
        if "fruits" in context.names:
            context.provided["fruits"] = ProvidedValue().withAnnotatedElementValueSet([
                AnnotatedValue("apple").withLabel("Apple"), AnnotatedValue("banana").withLabel("Banana"), AnnotatedValue("lemon").withLabel("Lemon")
            ])
