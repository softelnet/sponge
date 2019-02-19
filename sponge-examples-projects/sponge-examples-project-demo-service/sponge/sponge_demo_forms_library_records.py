"""
Sponge Knowledge base
Demo Forms - Library as records
"""

def createBookRecordType():
    return RecordType("Book", [
        RecordTypeField("id", IntegerType().withNullable()).withLabel("Identifier"),
        RecordTypeField("author", StringType()).withLabel("Author"),
        RecordTypeField("title", StringType()).withLabel("Title"),
    ])

class RecordLibraryForm(Action):
    def onConfigure(self):
        self.withLabel("Library (books as records)")
        self.withArgs([
            ArgMeta("search", StringType().withNullable()).withLabel("Search"),
            ArgMeta("order", StringType()).withLabel("Sort by").withProvided(ArgProvidedMeta().withValue().withValueSet()),
            # Provided with overwrite to allow GUI refresh.
            ArgMeta("books", ListType(AnnotatedType(createBookRecordType())).withFeatures({
                "createAction":"RecordCreateBook", "readAction":"RecordReadBook", "updateAction":"RecordUpdateBook", "deleteAction":"RecordDeleteBook",
                "createLabel":"Add", "readLabel":"View", "updateLabel":"Edit", "deleteLabel":"Remove",
            })).withLabel("Books").withProvided(ArgProvidedMeta().withValue().withOverwrite().withDependencies(["search", "order"]))
        ]).withNoResult()
        self.withFeatures({
            "callLabel":None, "refreshLabel":None, "clearLabel":None, "cancelLabel":None,
        })
    def onCall(self, search, order, books):
        return None
    def onProvideArgs(self, names, current, provided):
        global LIBRARY
        if "order" in names:
            provided["order"] = ArgProvidedValue().withValue("author").withAnnotatedValueSet([
                AnnotatedValue("author").withLabel("Author"), AnnotatedValue("title").withLabel("Title")])
        if "books" in names:
            provided["books"] = ArgProvidedValue().withValue(
                map(lambda book: AnnotatedValue(book.toMap()).withLabel("{} - {}".format(book.author, book.title)),
                    sorted(LIBRARY.findBooks(current["search"]), key = lambda book: book.author if current["order"] == "author" else book.title)))

class RecordCreateBook(Action):
    def onConfigure(self):
        self.withLabel("Add a book")
        self.withArgs([
            ArgMeta("book", createBookRecordType()).withLabel("Book").withSubArgs([
                ArgMeta("id").withFeature("visible", False),
                ArgMeta("author").withProvided(ArgProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, book):
        global LIBRARY
        LIBRARY.addBook(book["author"], book["title"])

    def onProvideArgs(self, names, current, provided):
        global LIBRARY
        if "book.author" in names:
            provided["book.author"] = ArgProvidedValue().withValueSet(LIBRARY.getAuthors())

class RecordAbstractReadUpdateBook(Action):
    def onConfigure(self):
        self.withArg(
            ArgMeta("book", AnnotatedType(createBookRecordType())).withSubArgs([
                ArgMeta("id").withFeature("visible", False),
                ArgMeta("author").withProvided(ArgProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ]).withProvided(ArgProvidedMeta().withValue().withDependency("book.id"))).withNoResult()
        self.withFeatures({"visible":False, "clearLabel":None})

    def onProvideArgs(self, names, current, provided):
        global LIBRARY
        if "book" in names:
            provided["book"] = ArgProvidedValue().withValue(AnnotatedValue(LIBRARY.getBook(current["book.id"]).toMap()))
        if "book.author" in names:
            provided["book.author"] = ArgProvidedValue().withValueSet(LIBRARY.getAuthors())

class RecordReadBook(RecordAbstractReadUpdateBook):
    def onConfigure(self):
        RecordAbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Read a book")
        self.withFeatures({"callLabel":None, "cancelLabel":"Close"})

    def onCall(self, bookId, author, title):
        pass

class RecordUpdateBook(RecordAbstractReadUpdateBook):
    def onConfigure(self):
        RecordAbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Modify a book")
        self.withFeatures({"callLabel":"Save", "cancelLabel":"Cancel"})

    def onCall(self, book):
        global LIBRARY
        LIBRARY.updateBook(book.value["id"], book.value["author"], book.value["title"])

class RecordDeleteBook(Action):
    def onConfigure(self):
        self.withLabel("Remove a book")
        self.withArg(ArgMeta("book", AnnotatedType(createBookRecordType()))).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, book):
        global LIBRARY
        self.logger.info("Deleting book id: {}", book.value["id"])
        LIBRARY.removeBook(book.value["id"])
