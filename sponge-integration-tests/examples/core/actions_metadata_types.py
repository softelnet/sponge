"""
Sponge Knowledge base
Action metadata types
"""

class MultipleArgumentsAction(Action):
    def onConfigure(self):
        self.withLabel("Multiple arguments action").withDescription("Multiple arguments action.")
        self.withArgs([
            StringType("stringArg").withMaxLength(10).withFormat("ipAddress"),
            IntegerType("integerArg").withMinValue(1).withMaxValue(100).withDefaultValue(50),
            AnyType("anyArg").withNullable(),
            ListType("stringListArg", StringType()),
            ListType("decimalListArg", ObjectType().withClassName("java.math.BigDecimal")),
            ObjectType("stringArrayArg").withClassName("java.lang.String[]"),
            ObjectType("javaClassArg").withClassName("org.openksavi.sponge.examples.CustomObject"),
            ListType("javaClassListArg", ObjectType().withClassName("org.openksavi.sponge.examples.CustomObject")),
            BinaryType("binaryArg").withMimeType("image/png").withFeatures({"width":28, "height":28, "background":"black", "color":"white"}),
            TypeType("typeArg"),
            DynamicType("dynamicArg")
        ])
        self.withResult(BooleanType().withLabel("Boolean result"))
    def onCall(self, stringArg, integerArg, anyArg, stringListArg, decimalListArg, stringArrayArg, javaClassArg, javaClassListArg, binaryArg, typeArg, dynamicArg):
        return True

class ActionReturningMap(Action):
    def onConfigure(self):
        self.withNoArgs().withResult(MapType(StringType(), IntegerType()).withLabel("Map result"))
    def onCall(self):
        return {"a":1, "b":2, "c":3}