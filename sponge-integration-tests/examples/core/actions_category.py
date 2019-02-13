"""
Sponge Knowledge base
Action category
"""

def onInit():
    sponge.addCategories(CategoryMeta("myActions").withLabel("My actions").withDescription("My actions description"),
                         CategoryMeta("yourActions").withLabel("Your actions").withDescription("Your actions description"),
                         CategoryMeta("notUsedCategory"))

class MyAction1(Action):
    def onConfigure(self):
        self.withLabel("MyAction 1").withCategory("myActions")
    def onCall(self, text):
    	return None

class MyAction2(Action):
    def onConfigure(self):
        self.withLabel("MyAction 2").withCategory("myActions")
    def onCall(self, text):
        return None

class YourAction1(Action):
    def onConfigure(self):
        self.withLabel("YourAction1 1").withCategory("yourActions")
    def onCall(self, text):
        return None

class OtherAction(Action):
    def onCall(self, text):
        return None
