"""
Sponge Knowledge base
REST API Complex object
"""

class ComplexObjectAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("arg", ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject")).displayName("Text to upper case")
        ]
        self.resultMeta = ResultMeta(ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject")).displayName("Upper case text")
    def onCall(self, arg):
        self.logger.info("Action {} called", self.name)
        arg.id += 1
        return arg

class ComplexObjectListAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("arg", ListType(ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject"))).displayName("Text to upper case")
        ]
        self.resultMeta = ResultMeta(ListType(ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject"))).displayName("Upper case text")
    def onCall(self, arg):
        self.logger.info("Action {} called: {}", self.name, arg)
        arg[0].id += 1
        return arg

class ComplexObjectHierarchyAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("stringArg", StringType()),
            ArgMeta("anyArg", AnyType()),
            ArgMeta("stringListArg", ListType(StringType())),
            ArgMeta("decimalListArg", ListType(ObjectType("java.math.BigDecimal"))),
            ArgMeta("stringArrayArg", ObjectType("java.lang.String[]")),
            ArgMeta("mapArg", MapType(StringType(), ObjectType("org.openksavi.sponge.examples.TestCompoundComplexObject")))
        ]
        self.resultMeta = ResultMeta(ListType(AnyType()))
    def onCall(self, stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg):
        self.logger.info("Action {} called: {}, {}, {}, {}, {}, {}", self.name, stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg)
        return [stringArg, anyArg, stringListArg, decimalListArg, stringArrayArg, mapArg]
