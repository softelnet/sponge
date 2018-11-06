"""
Sponge Knowledge base
Action metadata types
"""

class MultipleArgumentsAction(Action):
    def onConfigure(self):
        self.displayName = "Multiple arguments action"
        self.description = "Multiple arguments action."
        self.argsMeta = [
            ArgMeta("stringArg", StringType().maxLength(10).format("ipAddress")),
            ArgMeta("integerArg", IntegerType().minValue(1).maxValue(100).defaultValue(50)),
            ArgMeta("anyArg", AnyType().nullable(True)),
            ArgMeta("stringListArg", ListType(StringType())),
            ArgMeta("decimalListArg", ListType(ObjectType("java.math.BigDecimal"))),
            ArgMeta("stringArrayArg", ObjectType("java.lang.String[]")),
            ArgMeta("javaClassArg", ObjectType("org.openksavi.sponge.examples.CustomObject")),
            ArgMeta("javaClassListArg", ListType(ObjectType("org.openksavi.sponge.examples.CustomObject"))),
            ArgMeta("binaryArg", BinaryType().mimeType("image/png").features({"width":28, "height":28, "background":"black", "color":"white"})),
        ]
        self.resultMeta = ResultMeta(BooleanType()).displayName("Boolean result")
    def onCall(self, stringArg, integerArg, anyArg, stringListArg, decimalListArg, stringArrayArg, javaClassArg, javaClassListArg, binaryArg):
        return True

class ActionReturningMap(Action):
    def onConfigure(self):
        self.argsMeta = []
        self.resultMeta = ResultMeta(MapType(StringType(), IntegerType())).displayName("Map result")
    def onCall(self):
        return {"a":1, "b":2, "c":3}