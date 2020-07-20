"""
Sponge Knowledge Base
Action metadata types - loop
"""

class TypeLoopAction(Action):
    def onConfigure(self):
        listType = ListType("listArg", StringType())
        # Type loop will raise an exception.
        listType.withElement(listType)
        self.withArgs([
            listType
        ])
        self.withResult(BooleanType())
    def onCall(self, listArg):
        return True
