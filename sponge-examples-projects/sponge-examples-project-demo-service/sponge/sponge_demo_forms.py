"""
Sponge Knowledge base
Demo Forms
"""

from java.lang import System
from java.util.concurrent.atomic import AtomicInteger
from java.util.concurrent import CopyOnWriteArrayList
from java.util.function import Predicate

def onInit():
    global LIBRARY
    LIBRARY = createLibrary()

class ChangedButtonLabelsForm(Action):
    def onConfigure(self):
        self.withLabel("Changed button labels form")
        self.withArgs([
            ArgMeta("version", StringType().withNullable()).withLabel("Sponge version").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
        self.withFeatures({"callLabel":"Call", "refreshLabel":"Reload", "clearLabel":"Reset", "cancelLabel":"Close"})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, names, current, provided):
        if "version" in names:
            provided["version"] = ArgProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

class HiddenButtonsForm(Action):
    def onConfigure(self):
        self.withLabel("Hidden buttons form")
        self.withArgs([
            ArgMeta("version", StringType().withNullable()).withLabel("Sponge version").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
        self.withFeatures({"callLabel":"Call", "refreshLabel":None, "clearLabel":None, "cancelLabel":None})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, names, current, provided):
        if "version" in names:
            provided["version"] = ArgProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

class DefaultCallButtonForm(Action):
    def onConfigure(self):
        self.withLabel("Default label for the call button form")
        self.withArgs([
            ArgMeta("version", StringType().withNullable()).withLabel("Sponge version").withProvided(ArgProvidedMeta().withValue().withReadOnly()),
            ArgMeta("text", StringType().withMaxLength(256)).withLabel("Text to upper case").withDescription("The text that will be converted to upper case.")
        ]).withResult(ResultMeta(StringType()).withLabel("Upper case text"))
        self.withFeatures({"refreshLabel":None, "clearLabel":None, "cancelLabel":None})
    def onCall(self, version, text):
        return text.upper()
    def onProvideArgs(self, names, current, provided):
        if "version" in names:
            provided["version"] = ArgProvidedValue().withValue("{} [{}]".format(sponge.version, System.currentTimeMillis()))

# Domain class
class Book:
    def __init__(self, id, author, title):
        self.id = id
        self.author = author
        self.title = title
    def toMap(self):
        return {"id":id, "author":author, "title":title}
    def fromMap(bookAsMap):
        return Book(bookAsMap["id"], bookAsMap["author"], bookAsMap["title"])

# Jython hack [https://stackoverflow.com/questions/43970506/how-to-use-java-8-lambdas-in-jython]
class PyPredicate(Predicate):
   def __init__(self, fn):
       self.test = fn

# Sample data
class Library:
    def __init__(self):
        self.books = CopyOnWriteArrayList()
        self.currentId = AtomicInteger(0)

    def addBook(self, author, title):
        self.books.add(Book(self.currentId.incrementAndGet(), author, title))

    def getBook(self, bookId):
        return filter(lambda book: book.id == bookId, self.books)[0]

    def updateBook(self, bookId, author, title):
        book = self.getBook(bookId)
        book.author = author
        book.title = title

    def removeBook(self, bookId):
        self.books.removeIf(PyPredicate(lambda book: book.id == bookId))

def createLibrary():
    library = Library()
    library.addBook("James Joyce", "Ulysses")
    library.addBook("Arthur Conan Doyle", "Adventures of Sherlock Holmes")
    library.addBook("Charles Dickens", "A Christmas Carol")
    library.addBook("Alexandre Dumas", "Count of Monte Cristo")
    library.addBook("Miguel De Cervantes", "Don Quixote")
    library.addBook("Jane Austen", "Pride and Prejudice")
    library.addBook("Bram Stoker", "Dracula")
    library.addBook("Mary Wollstonecraft Shelley", "Frankenstein")
    library.addBook("Lewis Carroll", "Alice's Adventures in Wonderland")
    library.addBook("William Shakespeare", "The Complete Works of William Shakespeare")
    library.addBook("Oscar Wilde", "The Picture of Dorian Gray")
    library.addBook("Charles Dickens", "Great Expectations")
    library.addBook("Jules Verne", "Twenty Thousand Leagues Under the Sea")
    library.addBook("Edgar Allan Poe", "The Works of Edgar Allan Poe")
    library.addBook("George Orwell", "Nineteen-Eighty Four")
    library.addBook("Ray Bradbury", "Fahrenheit 451")
    library.addBook("Ernest Hemingway", "For Whom the Bell Tolls")
    return library

class ProvidedListArgForm(Action):
    def onConfigure(self):
        self.withLabel("Library")
        self.withArgs([
            # Provided with overwrite to allow GUI refresh.
            ArgMeta("books", ListType(AnnotatedType(IntegerType())).withFeatures({
                "createAction":"CreateBook", "updateAction":"UpdateBook", "deleteAction":"DeleteBook",
                "createLabel":"Add", "updateLabel":"Edit", "deleteLabel":"Remove",
            })).withLabel("Books").withProvided(ArgProvidedMeta().withValue().withOverwrite())
        ]).withNoResult()
        self.withFeatures({
            "callLabel":None, "refreshLabel":None, "clearLabel":None, "cancelLabel":None,
        })
    def onCall(self, books):
        return None
    def onProvideArgs(self, names, current, provided):
        if "books" in names:
            provided["books"] = ArgProvidedValue().withValue(
                map(lambda book: AnnotatedValue(int(book.id)).withLabel("{} - {}".format(book.author, book.title)), LIBRARY.books))

class CreateBook(Action):
    def onConfigure(self):
        self.withLabel("Add a book")
        self.withArgs([
            ArgMeta("author", StringType()).withLabel("Author"),
            ArgMeta("title", StringType()).withLabel("Title")
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, author, title):
        LIBRARY.addBook(author, title)

class UpdateBook(Action):
    def onConfigure(self):
        self.withLabel("Modify a book")
        self.withArgs([
            ArgMeta("bookId", AnnotatedType(IntegerType())).withFeature("visible", False),
            ArgMeta("author", StringType()).withLabel("Author").withProvided(ArgProvidedMeta().withValue().withDependency("bookId")),
            ArgMeta("title", StringType()).withLabel("Title").withProvided(ArgProvidedMeta().withValue().withDependency("bookId"))
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, bookId, author, title):
        LIBRARY.updateBook(bookId.value, author, title)

    def onProvideArgs(self, names, current, provided):
        if "author" or "title" in names:
            book = LIBRARY.getBook(current["bookId"].value)
            provided["author"] = ArgProvidedValue().withValue(book.author)
            provided["title"] = ArgProvidedValue().withValue(book.title)

class DeleteBook(Action):
    def onConfigure(self):
        self.withLabel("Remove a book")
        self.withArgs([
            ArgMeta("bookId", AnnotatedType(IntegerType())).withFeature("visible", False),
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "clearLabel":None, "cancelLabel":"Cancel"})

    def onCall(self, bookId):
        self.logger.info("Deleting book id: {}", bookId.value)
        LIBRARY.removeBook(bookId.value)
