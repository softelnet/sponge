"""
Sponge Knowledge base
Provide action arguments - element value set
"""

class FruitsElementValueSetAction(Action):
    def onConfigure(self):
        self.withLabel("Fruits action with argument element value set")
        self.withArg(ListType("fruits", StringType()).withLabel("Fruits").withUnique().withProvided(ProvidedMeta().withElementValueSet()))
        self.withResult(IntegerType())
    def onCall(self, fruits):
        return len(fruits)
    def onProvideArgs(self, context):
        if "fruits" in context.provide:
            context.provided["fruits"] = ProvidedValue().withAnnotatedElementValueSet([
                AnnotatedValue("apple").withValueLabel("Apple"), AnnotatedValue("banana").withValueLabel("Banana"),
                AnnotatedValue("lemon").withValueLabel("Lemon")
            ])
