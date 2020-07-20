"""
Sponge Knowledge Base
Action category selection
"""

def onInit():
    sponge.addCategories(CategoryMeta("myActions").withLabel("My actions").withDescription("My actions description"),
                         CategoryMeta("yourActions").withLabel("Your actions").withDescription("Your actions description"),
                         CategoryMeta("notUsedCategory"))
def onLoad():
    sponge.selectCategory("myActions", ProcessorType.ACTION, lambda processor: processor.meta.name in ("MyAction1", "MyAction2"))
    sponge.selectCategory("yourActions", lambda processor: processor.meta.name in ("YourAction1") and processor.kb.name == "kb")

class MyAction1(Action):
    def onConfigure(self):
        self.withLabel("MyAction 1")
    def onCall(self, text):
    	return None

class MyAction2(Action):
    def onConfigure(self):
        self.withLabel("MyAction 2").withCategory("yourActions")
    def onCall(self, text):
        return None

class YourAction1(Action):
    def onConfigure(self):
        self.withLabel("YourAction1 1")
    def onCall(self, text):
        return None

class OtherAction(Action):
    def onCall(self, text):
        return None
