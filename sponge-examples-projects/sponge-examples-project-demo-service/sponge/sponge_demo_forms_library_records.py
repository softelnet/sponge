"""
Sponge Knowledge base
Demo Forms - Library as records
"""

def createBookRecordType(name):
    return RecordType(name).withFields([
        IntegerType("id").withLabel("ID").withNullable().withFeature("visible", False),
        StringType("author").withLabel("Author"),
        StringType("title").withLabel("Title")
])


class RecordLibraryForm(Action):
    def onConfigure(self):
        self.withLabel("Library (books as records)")
        self.withArgs([
            StringType("search").withNullable().withLabel("Search"),
            StringType("order").withLabel("Sort by").withProvided(ProvidedMeta().withValue().withValueSet()),
            ListType("books").withLabel("Books").withFeatures({
                    "createAction":"RecordCreateBook", "readAction":"RecordReadBook", "updateAction":"RecordUpdateBook", "deleteAction":"RecordDeleteBook",
                    "createLabel":"Add", "readLabel":"View", "updateLabel":"Edit", "deleteLabel":"Remove",
                # Provided with overwrite to allow GUI refresh.
                }).withProvided(ProvidedMeta().withValue().withOverwrite().withDependencies(["search", "order"])).withElement(
                        createBookRecordType("book").withAnnotated()
                )
        ]).withNoResult().withCallable(False)
        self.withFeatures({
            "callLabel":None, "refreshLabel":None, "clearLabel":None, "cancelLabel":None,
        })
        self.withFeature("icon", "library-books")
    def onProvideArgs(self, context):
        global LIBRARY
        if "order" in context.names:
            context.provided["order"] = ProvidedValue().withValue("author").withAnnotatedValueSet([
                AnnotatedValue("author").withLabel("Author"), AnnotatedValue("title").withLabel("Title")])
        if "books" in context.names:
            context.provided["books"] = ProvidedValue().withValue(
                map(lambda book: AnnotatedValue(book.toMap()).withLabel("{} - {}".format(book.author, book.title)),
                    sorted(LIBRARY.findBooks(context.current["search"]), key = lambda book: book.author.lower() if context.current["order"] == "author" else book.title.lower())))

class RecordCreateBook(Action):
    def onConfigure(self):
        self.withLabel("Add a book")
        self.withArg(
            createBookRecordType("book").withLabel("Book").withProvided(ProvidedMeta().withValue()).withFields([
                StringType("author").withLabel("Author").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, book):
        global LIBRARY
        LIBRARY.addBook(book["author"], book["title"])

    def onProvideArgs(self, context):
        global LIBRARY
        if "book" in context.names:
            # Create an initial, blank instance of a book and provide it to GUI.
            context.provided["book"] = ProvidedValue().withValue({})
        if "book.author" in context.names:
            context.provided["book.author"] = ProvidedValue().withValueSet(LIBRARY.getAuthors())

class RecordReadBook(Action):
    def onConfigure(self):
        self.withLabel("Read a book")
        self.withArg(createBookRecordType("book").withAnnotated().withLabel("Book").withProvided(ProvidedMeta().withValue().withDependency("book.id")))
        self.withNoResult().withCallable(False)
        self.withFeatures({"visible":False, "clearLabel":None, "callLabel":None, "cancelLabel":"Close"})
    def onProvideArgs(self, context):
        global LIBRARY
        if "book" in context.names:
            context.provided["book"] = ProvidedValue().withValue(AnnotatedValue(LIBRARY.getBook(context.current["book.id"]).toMap()))

class RecordUpdateBook(Action):
    def onConfigure(self):
        self.withLabel("Modify a book")
        self.withArg(
            createBookRecordType("book").withAnnotated().withLabel("Book").withProvided(ProvidedMeta().withValue().withDependency("book.id")).withFields([
                StringType("author").withLabel("Author").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ).withNoResult()
        self.withFeatures({"visible":False, "clearLabel":None, "callLabel":"Save", "cancelLabel":"Cancel"})
    def onCall(self, book):
        global LIBRARY
        LIBRARY.updateBook(book.value["id"], book.value["author"], book.value["title"])
    def onProvideArgs(self, context):
        global LIBRARY
        if "book" in context.names:
            context.provided["book"] = ProvidedValue().withValue(AnnotatedValue(LIBRARY.getBook(context.current["book.id"]).toMap()))
        if "book.author" in context.names:
            context.provided["book.author"] = ProvidedValue().withValueSet(LIBRARY.getAuthors())

class RecordDeleteBook(Action):
    def onConfigure(self):
        self.withLabel("Remove a book")
        self.withArg(createBookRecordType("book").withAnnotated()).withNoResult()
        self.withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, book):
        global LIBRARY
        self.logger.info("Deleting book id: {}", book.value["id"])
        LIBRARY.removeBook(book.value["id"])
