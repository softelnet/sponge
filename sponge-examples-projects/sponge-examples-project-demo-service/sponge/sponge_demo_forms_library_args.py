"""
Sponge Knowledge base
Demo Forms - Library as action arguments
"""

class ArgLibraryForm(Action):
    def onConfigure(self):
        self.withLabel("Library (books as arguments)")
        self.withArgs([
            ArgMeta("search", StringType().withNullable()).withLabel("Search"),
            ArgMeta("order", StringType()).withLabel("Sort by").withProvided(ArgProvidedMeta().withValue().withValueSet()),
            # Provided with overwrite to allow GUI refresh.
            ArgMeta("books", ListType(AnnotatedType(IntegerType())).withFeatures({
                "createAction":"ArgCreateBook", "readAction":"ArgReadBook", "updateAction":"ArgUpdateBook", "deleteAction":"ArgDeleteBook",
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
                map(lambda book: AnnotatedValue(int(book.id)).withLabel("{} - {}".format(book.author, book.title)),
                    sorted(LIBRARY.findBooks(current["search"]), key = lambda book: book.author if current["order"] == "author" else book.title)))

class ArgCreateBook(Action):
    def onConfigure(self):
        self.withLabel("Add a book")
        self.withArgs([
            ArgMeta("author", StringType()).withLabel("Author"),
            ArgMeta("title", StringType()).withLabel("Title")
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, author, title):
        global LIBRARY
        LIBRARY.addBook(author, title)

class ArgAbstractReadUpdateBook(Action):
    def onConfigure(self):
        self.withArgs([
            ArgMeta("bookId", AnnotatedType(IntegerType())).withFeature("visible", False),
            ArgMeta("author", StringType()).withLabel("Author").withProvided(ArgProvidedMeta().withValue().withDependency("bookId")),
            ArgMeta("title", StringType()).withLabel("Title").withProvided(ArgProvidedMeta().withValue().withDependency("bookId"))
        ]).withNoResult()
        self.withFeatures({"visible":False, "clearLabel":None})

    def onProvideArgs(self, names, current, provided):
        global LIBRARY
        if "author" or "title" in names:
            book = LIBRARY.getBook(current["bookId"].value)
            provided["author"] = ArgProvidedValue().withValue(book.author)
            provided["title"] = ArgProvidedValue().withValue(book.title)

class ArgReadBook(ArgAbstractReadUpdateBook):
    def onConfigure(self):
        ArgAbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Read a book")
        self.withFeatures({"callLabel":None, "cancelLabel":"Close"})

    def onCall(self, bookId, author, title):
        pass

class ArgUpdateBook(ArgAbstractReadUpdateBook):
    def onConfigure(self):
        ArgAbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Modify a book")
        self.withFeatures({"callLabel":"Save", "cancelLabel":"Cancel"})

    def onCall(self, bookId, author, title):
        global LIBRARY
        LIBRARY.updateBook(bookId.value, author, title)

class ArgDeleteBook(Action):
    def onConfigure(self):
        self.withLabel("Remove a book")
        self.withArgs([
            ArgMeta("bookId", AnnotatedType(IntegerType())).withFeature("visible", False),
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, bookId):
        global LIBRARY
        self.logger.info("Deleting book id: {}", bookId.value)
        LIBRARY.removeBook(bookId.value)
