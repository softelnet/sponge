"""
Sponge Knowledge base
Demo Forms
"""

from java.lang import System
from java.util.concurrent.atomic import AtomicInteger
from java.util.concurrent import CopyOnWriteArrayList
from java.util.function import Predicate
import re

def onInit():
    global LIBRARY
    LIBRARY = createLibrary()
    sponge.setVariable("booksOrder", "author")

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
        if list(filter(lambda book: book.author == author and book.title == title, self.books)):
            raise Exception("This book has been added to the library")
        self.books.add(Book(self.currentId.incrementAndGet(), author, title))

    def getBook(self, bookId):
        return filter(lambda book: book.id == bookId, self.books)[0]

    def updateBook(self, bookId, author, title):
        book = self.getBook(bookId)
        book.author = author
        book.title = title

    def removeBook(self, bookId):
        self.books.removeIf(PyPredicate(lambda book: book.id == bookId))

    def findBooks(self, searchString):
        return list(filter(lambda book: searchString is None or re.search(searchString.upper(), book.author.upper())\
                            or re.search(searchString.upper(), book.title.upper()), self.books))

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
            ArgMeta("search", StringType().withNullable()).withLabel("Search"),
            ArgMeta("order", StringType()).withLabel("Sort by").withProvided(ArgProvidedMeta().withValue().withValueSet()),
            # Provided with overwrite to allow GUI refresh.
            ArgMeta("books", ListType(AnnotatedType(IntegerType())).withFeatures({
                "createAction":"CreateBook", "readAction":"ReadBook", "updateAction":"UpdateBook", "deleteAction":"DeleteBook",
                "createLabel":"Add", "readLabel":"View", "updateLabel":"Edit", "deleteLabel":"Remove",
            })).withLabel("Books").withProvided(ArgProvidedMeta().withValue().withOverwrite().withDependencies(["search", "order"]))
        ]).withNoResult()
        self.withFeatures({
            "callLabel":None, "refreshLabel":None, "clearLabel":None, "cancelLabel":None,
        })
    def onCall(self, search, order, books):
        return None
    def onProvideArgs(self, names, current, provided):
        if "order" in names:
            provided["order"] = ArgProvidedValue().withValue(sponge.getVariable("booksOrder")).withAnnotatedValueSet([
                AnnotatedValue("author").withLabel("Author"), AnnotatedValue("title").withLabel("Title")])
        if "books" in names:
            provided["books"] = ArgProvidedValue().withValue(
                map(lambda book: AnnotatedValue(int(book.id)).withLabel("{} - {}".format(book.author, book.title)),
                    sorted(LIBRARY.findBooks(current["search"]), key = lambda book: book.author if current["order"] == "author" else book.title)))

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

class AbstractReadUpdateBook(Action):
    def onConfigure(self):
        self.withArgs([
            ArgMeta("bookId", AnnotatedType(IntegerType())).withFeature("visible", False),
            ArgMeta("author", StringType()).withLabel("Author").withProvided(ArgProvidedMeta().withValue().withDependency("bookId")),
            ArgMeta("title", StringType()).withLabel("Title").withProvided(ArgProvidedMeta().withValue().withDependency("bookId"))
        ]).withNoResult()
        self.withFeatures({"visible":False, "clearLabel":None})

    def onProvideArgs(self, names, current, provided):
        if "author" or "title" in names:
            book = LIBRARY.getBook(current["bookId"].value)
            provided["author"] = ArgProvidedValue().withValue(book.author)
            provided["title"] = ArgProvidedValue().withValue(book.title)

class ReadBook(AbstractReadUpdateBook):
    def onConfigure(self):
        AbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Read a book")
        self.withFeatures({"callLabel":None, "cancelLabel":"Close"})

    def onCall(self, bookId, author, title):
        pass

class UpdateBook(AbstractReadUpdateBook):
    def onConfigure(self):
        AbstractReadUpdateBook.onConfigure(self)
        self.withLabel("Modify a book")
        self.withFeatures({"callLabel":"Save", "cancelLabel":"Cancel"})

    def onCall(self, bookId, author, title):
        LIBRARY.updateBook(bookId.value, author, title)

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
