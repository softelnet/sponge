"""
Sponge Knowledge base
Provide action arguments - element value set paging
"""

from java.util.concurrent import CopyOnWriteArrayList

def onInit():
    sponge.setVariable("allFruits", ["apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango",
                    "grapefruit", "kiwi", "plum", "pear", "strawberry", "blackberry", "pineapple", "papaya", "melon"])

class ViewFavouriteFruits(Action):
    def onConfigure(self):
        self.withLabel("Favourite fruits").withArgs([
            ListType("favouriteFruits", StringType()).withLabel("Favourite fruits").withProvided(
                ProvidedMeta().withElementValueSet()).withFeatures({"elementValueSetPaginable":True})
        ]).withNoResult().withCallable(False)
    def onProvideArgs(self, context):
        if "favouriteFruits" in context.provide:
            offset = context.features["favouriteFruits"].get("elementValueSetOffset")
            limit = context.features["favouriteFruits"].get("elementValueSetLimit")
            if offset is not None and limit is not None:
                context.provided["favouriteFruits"] = ProvidedValue().withElementValueSet(
                    sponge.getVariable("allFruits")[offset:(offset + limit)]).withFeatures(
                    {"elementValueSetOffset":offset, "elementValueSetLimit":limit})
            else:
                context.provided["favouriteFruits"] = ProvidedValue().withElementValueSet(sponge.getVariable("allFruits"))
