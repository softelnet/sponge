"""
Sponge Knowledge base
Provide action arguments - value paging
"""

from java.util.concurrent import CopyOnWriteArrayList

def onInit():
    values = CopyOnWriteArrayList()
    values.addAll(["apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango", "grapefruit", "kiwi", "plum"])
    sponge.setVariable("fruits", values)

class ViewFruits(Action):
    def onConfigure(self):
        self.withLabel("Fruits").withArgs([
            ListType("fruits", StringType()).withLabel("Fruits").withAnnotated().withProvided(ProvidedMeta().withValue()).withFeatures({
                "pageable":True})
        ]).withNoResult().withCallable(False)
    def onProvideArgs(self, context):
        if "fruits" in context.provide:
            offset = context.getFeature("fruits", "offset")
            limit = context.getFeature("fruits", "limit")

            fruits = sponge.getVariable("fruits")

            context.provided["fruits"] = ProvidedValue().withValue(AnnotatedValue(fruits[offset:(offset + limit)]).withFeatures(
                    {"offset":offset, "limit":limit, "count":len(fruits)}))
