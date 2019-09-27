"""
Sponge Knowledge base
Demo Forms - Library as action arguments
"""

class ArgLibraryForm(Action):
    def onConfigure(self):
        self.withLabel("Library (books as arguments)")
        self.withArgs([
            StringType("search").withNullable().withLabel("Search").withFeature("responsive", True),
            StringType("order").withLabel("Sort by").withProvided(ProvidedMeta().withValue().withValueSet()),
            # Provided with overwrite to allow GUI refresh.
            ListType("books").withLabel("Books").withProvided(ProvidedMeta().withValue().withOverwrite().withDependencies(["search", "order"])).withFeatures({
                "createAction":"ArgCreateBook", "readAction":"ArgReadBook", "updateAction":"ArgUpdateBook", "deleteAction":"ArgDeleteBook",
            }).withElement(
                IntegerType().withAnnotated()
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
                map(lambda book: AnnotatedValue(int(book.id)).withLabel("{} - {}".format(book.author, book.title)).withDescription("Sample description (ID: " + str(book.id) +")"),
                    sorted(LIBRARY.findBooks(context.current["search"]), key = lambda book: book.author.lower() if context.current["order"] == "author" else book.title.lower())))

class ArgCreateBook(Action):
    def onConfigure(self):
        self.withLabel("Add a new book")
        self.withArgs([
            StringType("author").withLabel("Author"),
            StringType("title").withLabel("Title")
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, author, title):
        global LIBRARY
        LIBRARY.addBook(author, title)

class ArgAbstractReadUpdateBook(Action):
    def onConfigure(self):
        self.withArgs([
            IntegerType("bookId").withAnnotated().withFeature("visible", False),
            StringType("author").withLabel("Author").withProvided(ProvidedMeta().withValue().withDependency("bookId")),
            StringType("title").withLabel("Title").withProvided(ProvidedMeta().withValue().withDependency("bookId"))
        ]).withNoResult()
        self.withFeatures({"visible":False, "clearLabel":None})

    def onProvideArgs(self, context):
        global LIBRARY
        if "author" or "title" in context.names:
            book = LIBRARY.getBook(context.current["bookId"].value)
            context.provided["author"] = ProvidedValue().withValue(book.author)
            context.provided["title"] = ProvidedValue().withValue(book.title)

class ArgReadBook(ArgAbstractReadUpdateBook):
    def onConfigure(self):
        ArgAbstractReadUpdateBook.onConfigure(self)
        self.withLabel("View the book").withCallable(False)
        self.withFeatures({"callLabel":None, "cancelLabel":"Close"})

    def onCall(self, bookId, author, title):
        pass

class ArgUpdateBook(ArgAbstractReadUpdateBook):
    def onConfigure(self):
        ArgAbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Modify the book")
        self.withFeatures({"callLabel":"Save", "cancelLabel":"Cancel"})

    def onCall(self, bookId, author, title):
        global LIBRARY
        LIBRARY.updateBook(bookId.value, author, title)

class ArgDeleteBook(Action):
    def onConfigure(self):
        self.withLabel("Remove the book")
        self.withArgs([
            IntegerType("bookId").withAnnotated().withFeature("visible", False),
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, bookId):
        global LIBRARY
        self.logger.info("Deleting book id: {}", bookId.value)
        LIBRARY.removeBook(bookId.value)
