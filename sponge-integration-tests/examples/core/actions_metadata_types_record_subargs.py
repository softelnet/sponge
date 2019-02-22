"""
Sponge Knowledge base
Action metadata Record type - sub-arguments
"""

def createBookRecordType():
    return RecordType("Book", [
        RecordTypeField("id", IntegerType().withNullable()).withLabel("Identifier"),
        RecordTypeField("author", StringType()).withLabel("Author"),
        RecordTypeField("title", StringType()).withLabel("Title"),
    ])

class Library(Action):
    def onConfigure(self):
        self.withArgs([
            ArgMeta("books", ListType(AnnotatedType(createBookRecordType())).withFeatures({
                "createAction":"RecordCreateBook", "readAction":"RecordReadBook", "updateAction":"RecordUpdateBook", "deleteAction":"RecordDeleteBook",
            })).withLabel("Books").withProvided(ArgProvidedMeta().withValue())
        ]).withNoResult()
    def onCall(self, search, order, books):
        return None
    def onProvideArgs(self, context):
        if "books" in context.names:
            context.provided["books"] = ArgProvidedValue().withValue([
                {"id":1, "author":"James Joyce", "title":"Ulysses"},
                {"id":2, "author":"Arthur Conan Doyle", "title":"Adventures of Sherlock Holmes"}
            ])

class CreateBook(Action):
    def onConfigure(self):
        self.withArgs([
            ArgMeta("book", createBookRecordType()).withLabel("Book").withSubArgs([
                ArgMeta("id").withFeature("visible", False),
                ArgMeta("author").withProvided(ArgProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ]).withNoResult()
    def onCall(self, book):
        pass
    def onProvideArgs(self, context):
        if "book.author" in context.names:
            context.provided["book.author"] = ArgProvidedValue().withValueSet(["James Joyce", "Arthur Conan Doyle"])

class UpdateBook(Action):
    def onConfigure(self):
        self.withArg(
            ArgMeta("book", AnnotatedType(createBookRecordType())).withSubArgs([
                ArgMeta("id").withFeature("visible", False),
                ArgMeta("author").withProvided(ArgProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ]).withProvided(ArgProvidedMeta().withValue().withDependency("book.id"))).withNoResult()
    def onProvideArgs(self, context):
        if "book" in context.names:
            context.provided["book"] = ArgProvidedValue().withValue(AnnotatedValue({"id":context.current["book.id"], "author":"James Joyce", "title":"Ulysses"}))
        if "book.author" in context.names:
            context.provided["book.author"] = ArgProvidedValue().withValueSet(["James Joyce", "Arthur Conan Doyle"])
    def onCall(self, book):
        pass
