"""
Sponge Knowledge base
Action metadata types
"""

class MultipleArgumentsAction(Action):
    def onConfigure(self):
        self.label = "Multiple arguments action"
        self.description = "Multiple arguments action."
        self.argsMeta = [
            ArgMeta("stringArg", StringType().withMaxLength(10).withFormat("ipAddress")),
            ArgMeta("integerArg", IntegerType().withMinValue(1).withMaxValue(100).withDefaultValue(50)),
            ArgMeta("anyArg", AnyType().withNullable(True)),
            ArgMeta("stringListArg", ListType(StringType())),
            ArgMeta("decimalListArg", ListType(ObjectType("java.math.BigDecimal"))),
            ArgMeta("stringArrayArg", ObjectType("java.lang.String[]")),
            ArgMeta("javaClassArg", ObjectType("org.openksavi.sponge.examples.CustomObject")),
            ArgMeta("javaClassListArg", ListType(ObjectType("org.openksavi.sponge.examples.CustomObject"))),
            ArgMeta("binaryArg", BinaryType().withMimeType("image/png").withFeatures({"width":28, "height":28, "background":"black", "color":"white"})),
            ArgMeta("typeArg", TypeType()),
            ArgMeta("dynamicArg", DynamicType()),
        ]
        self.resultMeta = ResultMeta(BooleanType()).withLabel("Boolean result")
    def onCall(self, stringArg, integerArg, anyArg, stringListArg, decimalListArg, stringArrayArg, javaClassArg, javaClassListArg, binaryArg, typeArg, dynamicArg):
        return True

class ActionReturningMap(Action):
    def onConfigure(self):
        self.argsMeta = []
        self.resultMeta = ResultMeta(MapType(StringType(), IntegerType())).withLabel("Map result")
    def onCall(self):
        return {"a":1, "b":2, "c":3}