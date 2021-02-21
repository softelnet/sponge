"""
Sponge Knowledge Base
Action category selection
"""

def onInit():
    sponge.addCategories(
        CategoryMeta("myActions").withLabel("My actions").withDescription("My actions description")
            .withPredicate(lambda processor: processor.adapter.type == ProcessorType.ACTION and processor.meta.name in ("MyAction1", "MyAction2")),
        CategoryMeta("yourActions").withLabel("Your actions").withDescription("Your actions description")
            .withPredicate(lambda processor: processor.meta.name in ("YourAction1") and processor.kb.name == "kb"),
        CategoryMeta("notUsedCategory"))

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
