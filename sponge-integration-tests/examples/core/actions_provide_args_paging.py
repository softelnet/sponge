"""
Sponge Knowledge base
Provide action arguments - value paging
"""

from java.util.concurrent import CopyOnWriteArrayList

def onInit():
    values = CopyOnWriteArrayList()
    values.addAll(["apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango", "grapefruit", "kiwi", "plum"])
    sponge.setVariable("fruits", values)
    sponge.setVariable("allFruits", ["apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango",
                    "grapefruit", "kiwi", "plum", "pear", "strawberry", "blackberry", "pineapple", "papaya", "melon"])

class ViewFruits(Action):
    def onConfigure(self):
        self.withLabel("Fruits").withArgs([
            ListType("fruits", StringType()).withLabel("Fruits").withProvided(ProvidedMeta().withValue()).withFeatures({"valuePaginable":True}),
            StringType("favouriteFruit").withLabel("Favourite fruit").withProvided(ProvidedMeta().withValueSet()).withFeatures({"valueSetPaginable":True})
        ]).withNoResult().withCallable(False)
    def onProvideArgs(self, context):
        if "fruits" in context.provide:
            valueOffset = context.features["fruits"].get("valueOffset")
            valueLimit = context.features["fruits"].get("valueLimit")
            if valueOffset is not None and valueLimit is not None:
                context.provided["fruits"] = ProvidedValue().withValue(sponge.getVariable("fruits")[valueOffset:(valueOffset + valueLimit)]).withFeatures(
                    {"valueOffset":valueOffset, "valueLimit":valueLimit})
            else:
                context.provided["fruits"] = ProvidedValue().withValue(sponge.getVariable("fruits"))
        if "favouriteFruit" in context.provide:
            valueSetOffset = context.features["favouriteFruit"].get("valueSetOffset")
            valueSetLimit = context.features["favouriteFruit"].get("valueSetLimit")
            if valueSetOffset is not None and valueSetLimit is not None:
                context.provided["favouriteFruit"] = ProvidedValue().withValueSet(
                    sponge.getVariable("allFruits")[valueSetOffset:(valueSetOffset + valueSetLimit)]).withFeatures(
                    {"valueSetOffset":valueSetOffset, "valueSetLimit":valueSetLimit})
            else:
                context.provided["favouriteFruit"] = ProvidedValue().withValueSet(sponge.getVariable("allFruits"))
