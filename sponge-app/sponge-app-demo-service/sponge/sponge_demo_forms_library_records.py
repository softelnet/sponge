"""
Sponge Knowledge base
Demo Forms - Library as records
"""
from org.openksavi.sponge.util.process import ProcessConfiguration

def createBookRecordType(name):
    """ Creates a book record type.
    """
    return RecordType(name).withFields([
        IntegerType("id").withLabel("ID").withNullable().withFeature("visible", False),
        StringType("author").withLabel("Author"),
        StringType("title").withLabel("Title"),
        StringType("cover").withNullable().withReadOnly().withFeatures({"characteristic":"networkImage"}),
])

class RecordLibraryForm(Action):
    def onConfigure(self):
        self.withLabel("Library (books as records)")
        self.withArgs([
            StringType("search").withNullable().withLabel("Search").withFeature("responsive", True),
            StringType("order").withLabel("Sort by").withProvided(ProvidedMeta().withValue().withValueSet()),
            ListType("books").withLabel("Books").withElement(createBookRecordType("book").withAnnotated()).withFeatures({
                    "createAction":SubAction("RecordCreateBook"),
                    "readAction":SubAction("RecordReadBook").withArg("book", "this"),
                    "updateAction":SubAction("RecordUpdateBook").withArg("book", "this"),
                    "deleteAction":SubAction("RecordDeleteBook").withArg("book", "this"),
                    "refreshable":True,
                # Provided with overwrite to allow GUI refresh.
                }).withProvided(ProvidedMeta().withValue().withOverwrite().withDependencies(["search", "order"]))
        ]).withCallable(False).withFeature("icon", "library")
    def onProvideArgs(self, context):
        global LIBRARY
        if "order" in context.provide:
            context.provided["order"] = ProvidedValue().withValue("author").withAnnotatedValueSet([
                AnnotatedValue("author").withValueLabel("Author"), AnnotatedValue("title").withValueLabel("Title")])
        if "books" in context.provide:
            context.provided["books"] = ProvidedValue().withValue(
                # Context actions are provided dynamically in an annotated value.
                map(lambda book: AnnotatedValue(book.toMap()).withValueLabel("{} - {}".format(book.author, book.title)).withFeatures({
                    "contextActions":[
                        SubAction("RecordBookContextBinaryResult").withArg("book", "this"),
                        SubAction("RecordBookContextNoResult").withArg("book", "this"),
                        SubAction("RecordBookContextAdditionalArgs").withArg("book", "this")
                    ],
                    "icon":(IconInfo().withUrl(book.cover) if book.cover else None)}),
                    sorted(LIBRARY.findBooks(context.current["search"]), key = lambda book: book.author.lower() if context.current["order"] == "author" else book.title.lower())))

class RecordCreateBook(Action):
    def onConfigure(self):
        self.withLabel("Add a new book")
        self.withArg(
            createBookRecordType("book").withLabel("Book").withProvided(ProvidedMeta().withValue()).withFields([
                # Overwrite the author and cover fields.
                StringType("author").withLabel("Author").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
                StringType("cover").withNullable().withFeatures({"visible":False}),
            ])
        ).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"plus-box"})

    def onCall(self, book):
        global LIBRARY
        LIBRARY.addBook(book["author"], book["title"])

    def onProvideArgs(self, context):
        global LIBRARY
        if "book" in context.provide:
            # Create an initial, blank instance of a book and provide it to GUI.
            context.provided["book"] = ProvidedValue().withValue({})
        if "book.author" in context.provide:
            context.provided["book.author"] = ProvidedValue().withValueSet(LIBRARY.getAuthors())

class RecordReadBook(Action):
    def onConfigure(self):
        self.withLabel("View the book")
        # Must set withOverwrite to replace with the current value.
        self.withArg(createBookRecordType("book").withAnnotated().withLabel("Book").withReadOnly().withProvided(
            ProvidedMeta().withValue().withOverwrite().withDependency("book.id")))
        self.withCallable(False).withFeatures({"visible":False, "cancelLabel":"Close", "icon":"book-open"})
    def onProvideArgs(self, context):
        global LIBRARY
        if "book" in context.provide:
            context.provided["book"] = ProvidedValue().withValue(AnnotatedValue(LIBRARY.getBook(context.current["book.id"]).toMap()))

class RecordUpdateBook(Action):
    def onConfigure(self):
        self.withLabel("Modify the book")
        self.withArg(
            # Must set withOverwrite to replace with the current value.
            createBookRecordType("book").withAnnotated().withLabel("Book").withProvided(
                    ProvidedMeta().withValue().withOverwrite().withDependency("book.id")).withFields([
                StringType("author").withLabel("Author").withProvided(ProvidedMeta().withValueSet(ValueSetMeta().withNotLimited())),
            ])
        ).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"square-edit-outline"})
    def onCall(self, book):
        global LIBRARY
        LIBRARY.updateBook(book.value["id"], book.value["author"], book.value["title"], book.value["cover"])
    def onProvideArgs(self, context):
        global LIBRARY
        if "book" in context.provide:
            context.provided["book"] = ProvidedValue().withValue(AnnotatedValue(LIBRARY.getBook(context.current["book.id"]).toMap()))
        if "book.author" in context.provide:
            context.provided["book.author"] = ProvidedValue().withValueSet(LIBRARY.getAuthors())

class RecordDeleteBook(Action):
    def onConfigure(self):
        self.withLabel("Remove the book")
        self.withArg(createBookRecordType("book").withAnnotated().withFeature("visible", False)).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"delete", "confirmation":True})

    def onCall(self, book):
        global LIBRARY
        self.logger.info("Deleting book id: {}", book.value["id"])
        LIBRARY.removeBook(book.value["id"])

class RecordBookContextBinaryResult(Action):
    def onConfigure(self):
        self.withLabel("Text sample as PDF")
        self.withArg(
            createBookRecordType("book").withAnnotated().withFeature("visible", False)
        ).withResult(BinaryType().withAnnotated().withMimeType("application/pdf").withLabel("PDF"))
        self.withFeatures({"visible":False, "icon":"file-pdf"})
    def onCall(self, book):
        return AnnotatedValue(sponge.process(ProcessConfiguration.builder("curl", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                              .outputAsBinary()).run().outputBinary)

class RecordBookContextNoResult(Action):
    def onConfigure(self):
        self.withLabel("Return the book")
        self.withArg(
            createBookRecordType("book").withAnnotated().withFeature("visible", False)
        ).withNoResult().withFeatures({"visible":False, "icon":"arrow-left-bold"})
    def onCall(self, book):
        pass

class RecordBookContextAdditionalArgs(Action):
    def onConfigure(self):
        self.withLabel("Add book comment")
        self.withArgs([
            createBookRecordType("book").withAnnotated().withFeature("visible", False),
            StringType("comment").withLabel("Comment").withFeatures({"multiline":True, "maxLines":2})
        ]).withResult(StringType().withLabel("Added comment"))
        self.withFeatures({"visible":False, "icon":"comment-outline"})
    def onCall(self, book, message):
        return message
