"""
Sponge Knowledge base
Demo Forms - Library common
"""

from java.util.concurrent.atomic import AtomicInteger
from java.util.concurrent import CopyOnWriteArrayList
import re

def onInit():
    global LIBRARY
    LIBRARY = createLibrary()

def createLibrary():
    library = Library()
    library.readOnly = sponge.getVariable("demo.readOnly", False)

    library.addBook("James Joyce", "Ulysses", True)
    library.addBook("Arthur Conan Doyle", "Adventures of Sherlock Holmes", True)
    library.addBook("Charles Dickens", "A Christmas Carol", True)
    library.addBook("Alexandre Dumas", "Count of Monte Cristo", True)
    library.addBook("Miguel De Cervantes", "Don Quixote", True)
    library.addBook("Jane Austen", "Pride and Prejudice", True)
    library.addBook("Bram Stoker", "Dracula", True)
    library.addBook("Mary Wollstonecraft Shelley", "Frankenstein", True)
    library.addBook("Lewis Carroll", "Alice's Adventures in Wonderland", True)
    library.addBook("William Shakespeare", "The Complete Works of William Shakespeare", True)
    library.addBook("Oscar Wilde", "The Picture of Dorian Gray", True)
    library.addBook("Charles Dickens", "Great Expectations", True)
    library.addBook("Jules Verne", "Twenty Thousand Leagues Under the Sea", True)
    library.addBook("Edgar Allan Poe", "The Works of Edgar Allan Poe", True)
    library.addBook("George Orwell", "Nineteen-Eighty Four", True)
    library.addBook("Ray Bradbury", "Fahrenheit 451", True)
    library.addBook("Ernest Hemingway", "For Whom the Bell Tolls", True)
    return library

# Domain class
class Book:
    def __init__(self, id, author, title):
        self.id = id
        self.author = author
        self.title = title
    def toMap(self):
        return {"id":self.id, "author":self.author, "title":self.title}
    def fromMap(bookAsMap):
        return Book(bookAsMap["id"], bookAsMap["author"], bookAsMap["title"])

# Sample data
class Library:
    def __init__(self):
        self.books = CopyOnWriteArrayList()
        self.currentId = AtomicInteger(0)
        self.readOnly = False

    def addBook(self, author, title, force = False):
        if list(filter(lambda book: book.author == author and book.title == title, self.books)):
            raise Exception("This book has already been added to the library")
        if not self.readOnly or force:
            self.books.add(Book(self.currentId.incrementAndGet(), author, title))

    def getBook(self, bookId):
        return filter(lambda book: book.id == bookId, self.books)[0]

    def updateBook(self, bookId, author, title):
        book = self.getBook(bookId)
        if not self.readOnly:
            book.author = author
            book.title = title

    def removeBook(self, bookId):
        if not self.readOnly:
            self.books.removeIf(PyPredicate(lambda book: book.id == bookId))

    def findBooks(self, searchString):
        return list(filter(lambda book: searchString is None or re.search(searchString.upper(), book.author.upper())\
                            or re.search(searchString.upper(), book.title.upper()), self.books))

    def getAuthors(self):
        return sorted(list(set(list(map(lambda book: book.author, self.books)))), key = lambda author: author.lower())
