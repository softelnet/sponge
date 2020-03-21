"""
Sponge Knowledge base
Demo - A pageable list
"""

class ActionWithPageableList(Action):
    def onConfigure(self):
        self.withLabel("Action with a pageable list")
        self.withArgs([
            ListType("list").withLabel("List").withAnnotated().withFeatures({"pageable":True, "refreshable":True}).withProvided(
                ProvidedMeta().withValue().withOverwrite()
                ).withElement(
                    StringType("element").withAnnotated()
                )
        ]).withCallable(False).withFeatures({"icon":"library"})

    def onInit(self):
        self.elements = list(map(lambda n: AnnotatedValue(n).withValueLabel("Element " + str(n)), range(0, 25)))
        self.selected = 5

    def onProvideArgs(self, context):
        if "list" in context.provide:
            offset = context.getFeature("list", "offset")
            limit = context.getFeature("list", "limit")

            allElements = list(self.elements)
            allElements[self.selected].withFeature("icon", "star")

            page = allElements[offset:(offset + limit)]

            context.provided["list"] = ProvidedValue().withValue(AnnotatedValue(page).withTypeLabel(
                    "List" + ((" (" + str(len(allElements)) +")") if len(allElements) > 0 else "")).withFeatures(
                        {"offset":offset, "limit":limit, "count":len(allElements)}))
