"""
Sponge Knowledge base
Demo Forms - Library common
"""

from java.util.concurrent.atomic import AtomicInteger
from java.util.concurrent import CopyOnWriteArrayList
from java.util.function import Predicate
import re

def onInit():
    global LIBRARY
    LIBRARY = createLibrary()

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

    def getAuthors(self):
        return sorted(list(map(lambda book: book.author, self.books)))
