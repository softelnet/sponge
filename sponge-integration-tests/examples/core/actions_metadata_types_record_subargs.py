"""
Sponge Knowledge base
Action metadata Record type - sub-arguments
"""

def createBookRecordType(name):
    return RecordType(name, [
        IntegerType("id").withNullable().withLabel("Identifier").withFeature("visible", False),
        StringType("author").withLabel("Author"),
        StringType("title").withLabel("Title"),
    ])

class Library(Action):
    def onConfigure(self):
        self.withArgs([
            ListType("books").withLabel("Books").withProvided(ProvidedMeta().withValue()).withFeatures({
                    "createAction":"RecordCreateBook", "readAction":"RecordReadBook", "updateAction":"RecordUpdateBook", "deleteAction":"RecordDeleteBook",
                }).withElement(
                    createBookRecordType("book").withAnnotated()
                )
        ]).withNoResult()
    def onCall(self, search, order, books):
        return None
    def onProvideArgs(self, context):
        if "books" in context.names:
            context.provided["books"] = ProvidedValue().withValue([
                {"id":1, "author":"James Joyce", "title":"Ulysses"},
                {"id":2, "author":"Arthur Conan Doyle", "title":"Adventures of Sherlock Holmes"}
            ])

class CreateBook(Action):
    def onConfigure(self):
        self.withArgs([
            createBookRecordType("book").withLabel("Book").withFields([
                StringType("author").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ]).withNoResult()
    def onCall(self, book):
        pass
    def onProvideArgs(self, context):
        if "book.author" in context.names:
            context.provided["book.author"] = ProvidedValue().withValueSet(["James Joyce", "Arthur Conan Doyle"])

class UpdateBook(Action):
    def onConfigure(self):
        self.withArg(
            createBookRecordType("book").withAnnotated().withProvided(ProvidedMeta().withValue().withDependency("book.id")).withFields([
                StringType("author").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ).withNoResult()
    def onProvideArgs(self, context):
        if "book" in context.names:
            context.provided["book"] = ProvidedValue().withValue(AnnotatedValue({"id":context.current["book.id"], "author":"James Joyce", "title":"Ulysses"}))
        if "book.author" in context.names:
            context.provided["book.author"] = ProvidedValue().withValueSet(["James Joyce", "Arthur Conan Doyle"])
    def onCall(self, book):
        pass
