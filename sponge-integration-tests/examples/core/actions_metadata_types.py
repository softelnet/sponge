"""
Sponge Knowledge base
Action metadata types
"""

class MultipleArgumentsAction(Action):
    def onConfigure(self):
        self.displayName = "Multiple arguments action"
        self.description = "Multiple arguments action."
        self.argsMeta = [
            ArgMeta("stringArg", StringType().maxLength(10)),
            ArgMeta("integerArg", IntegerType().minValue(1).maxValue(100)),
            ArgMeta("anyArg", AnyType()),
            ArgMeta("stringListArg", ListType(StringType())),
            ArgMeta("decimalListArg", ListType(ObjectType("java.math.BigDecimal"))),
            ArgMeta("stringArrayArg", ObjectType("java.lang.String[]")),
            ArgMeta("javaClassArg", ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject")),
            ArgMeta("javaClassListArg", ListType(ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject"))),
            ArgMeta("binaryArg", BinaryType().format("jpg")),
        ]
        self.resultMeta = ResultMeta(BooleanType()).displayName("Boolean result")
    def onCall(self, stringArg, integerArg, anyArg, stringListArg, decimalListArg, stringArrayArg, javaClassArg, javaClassListArg, binaryArg):
        return True

