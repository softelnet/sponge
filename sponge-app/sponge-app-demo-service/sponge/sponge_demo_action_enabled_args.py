"""
Sponge Knowledge base
Demo - Action - enabling args
"""

class EnableArgsAction(Action):
    def onConfigure(self):
        self.withLabel("Enable args action").withArgs([
            BooleanType("enable").withLabel("Enable").withDefaultValue(False).withFeature("widget", "switch"),
            StringType("staticallyDisabled").withLabel("Statically disabled").withDefaultValue("Value").withFeature("enabled", False),
            StringType("dynamicallyDisabled").withLabel("Dynamically disabled").withAnnotated().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("enable").withCurrent().withLazyUpdate()),
            ListType("list").withLabel("List").withAnnotated().withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("enable")).withElement(
                    StringType("element").withAnnotated()).withFeature("refreshable", True),
            RecordType("record").withLabel("Record").withAnnotated().withFields([
                IntegerType("id").withLabel("ID").withNullable().withFeature("visible", False),
                StringType("author").withLabel("Author"),
                StringType("title").withLabel("Title")
            ]).withProvided(ProvidedMeta().withValue().withDependency("enable").withCurrent().withLazyUpdate().withOverwrite()),
            ListType("listOfRecords").withLabel("List of records").withAnnotated().withProvided(
                ProvidedMeta().withValue().withDependency("enable").withOverwrite().withLazyUpdate()).withElement(
                    RecordType("record").withLabel("Record").withAnnotated().withFields([
                        IntegerType("id").withLabel("ID").withNullable().withFeature("visible", False),
                        StringType("author").withLabel("Author"),
                        StringType("title").withLabel("Title")
                    ]).withFeature("refreshable", True)
            ),
        ]).withNonCallable()
    def onProvideArgs(self, context):
        if "dynamicallyDisabled" in context.provide:
            context.provided["dynamicallyDisabled"] = ProvidedValue().withValue(AnnotatedValue(
                context.current["dynamicallyDisabled"].value if context.current["dynamicallyDisabled"] else None).withFeature(
                    "enabled", context.current["enable"]))
        if "list" in context.provide:
            context.provided["list"] = ProvidedValue().withValue(AnnotatedValue(
                map(lambda element: AnnotatedValue(element).withFeature("contextActions", [SubAction("UpperCase")]),
                    ["a", "b", "c"])).withFeature("enabled", context.current["enable"]))
        if "record" in context.provide:
            context.provided["record"] = ProvidedValue().withValue(AnnotatedValue(context.current["record"].value if context.current["record"] else None).withFeature(
                "enabled", context.current["enable"]))
        if "listOfRecords" in context.provide:
            elements = [{"id":1, "author":"James Joyce", "title":"Ulysses"}, {"id":2, "author":"Arthur Conan Doyle", "title":"Adventures of Sherlock Holmes"}]
            context.provided["listOfRecords"] = ProvidedValue().withValue(AnnotatedValue(
                map(lambda element: AnnotatedValue(element).withFeature("contextActions", [SubAction("UpperCase")]), elements)).withFeature("enabled", context.current["enable"]))
