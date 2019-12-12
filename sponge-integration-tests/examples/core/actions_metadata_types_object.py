"""
Sponge Knowledge base
Action metadata types
"""

def createTestObjectType(name = None):
    return ObjectType(name).withClassName("org.openksavi.sponge.examples.CustomObject").withCompanionType(RecordType().withFields([
            IntegerType("id").withLabel("ID"),
            StringType("name").withLabel("Name")
        ]))

class ObjectTypeWithCompanionTypeAction(Action):
    def onConfigure(self):
        self.withLabel("Object type with companion type").withArgs([
            createTestObjectType("customObject"),
        ]).withResult(createTestObjectType())
    def onCall(self, customObject):
        if customObject.name:
            customObject.name = customObject.name.upper()
        return customObject
