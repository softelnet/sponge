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

    library.addBook("James Joyce", "Ulysses", "https://covers.openlibrary.org/b/olid/OL6652730M-M.jpg", force = True)
    library.addBook("Arthur Conan Doyle", "Adventures of Sherlock Holmes", "https://covers.openlibrary.org/b/olid/OL27902085M-M.jpg", force = True)
    library.addBook("Charles Dickens", "A Christmas Carol", "https://covers.openlibrary.org/b/olid/OL7157657M-M.jpg", force = True)
    library.addBook("Alexandre Dumas", "Count of Monte Cristo", "https://covers.openlibrary.org/w/id/8317270-M.jpg", force = True)
    library.addBook("Miguel De Cervantes", "Don Quixote", "https://covers.openlibrary.org/b/olid/OL26332614M-M.jpg", force = True)
    library.addBook("Jane Austen", "Pride and Prejudice", "https://covers.openlibrary.org/b/olid/OL7173379M-M.jpg", force = True)
    library.addBook("Bram Stoker", "Dracula", "https://covers.openlibrary.org/b/olid/OL8547453M-M.jpg", force = True)
    library.addBook("Mary Wollstonecraft Shelley", "Frankenstein", "https://covers.openlibrary.org/b/olid/OL7577065M-M.jpg", force = True)
    library.addBook("Lewis Carroll", "Alice's Adventures in Wonderland", "https://covers.openlibrary.org/b/olid/OL13442629M-M.jpg", force = True)
    library.addBook("William Shakespeare", "The Complete Works of William Shakespeare", "https://covers.openlibrary.org/b/olid/OL8978985M-M.jpg", force = True)
    library.addBook("Oscar Wilde", "The Picture of Dorian Gray", "https://covers.openlibrary.org/b/olid/OL1709882M-M.jpg", force = True)
    library.addBook("Charles Dickens", "Great Expectations", "https://covers.openlibrary.org/b/olid/OL24364628M-M.jpg", force = True)
    library.addBook("Jules Verne", "Twenty Thousand Leagues Under the Sea", "https://covers.openlibrary.org/b/olid/OL25200226M-M.jpg", force = True)
    library.addBook("Edgar Allan Poe", "The Works of Edgar Allan Poe", "https://covers.openlibrary.org/b/olid/OL7097254M-M.jpg", force = True)
    library.addBook("George Orwell", "Nineteen-Eighty Four", "https://covers.openlibrary.org/b/olid/OL27302768M-M.jpg", force = True)
    library.addBook("Ray Bradbury", "Fahrenheit 451", "https://covers.openlibrary.org/b/olid/OL18937344M-M.jpg", force = True)
    library.addBook("Ernest Hemingway", "For Whom the Bell Tolls", "https://covers.openlibrary.org/b/olid/OL4444289M-M.jpg", force = True)
    return library

# Domain class
class Book:
    def __init__(self, id, author, title, cover = None):
        self.id = id
        self.author = author
        self.title = title
        self.cover = cover
    def toMap(self):
        return {"id":self.id, "author":self.author, "title":self.title, "cover":self.cover}
    def fromMap(bookAsMap):
        return Book(bookAsMap["id"], bookAsMap["author"], bookAsMap["title"], bookAsMap["cover"])

# Sample data
class Library:
    def __init__(self):
        self.books = CopyOnWriteArrayList()
        self.currentId = AtomicInteger(0)
        self.readOnly = False

    def addBook(self, author, title, cover = None, force = False):
        if list(filter(lambda book: book.author == author and book.title == title, self.books)):
            raise Exception("This book has already been added to the library")
        if not self.readOnly or force:
            self.books.add(Book(self.currentId.incrementAndGet(), author, title, cover))

    def getBook(self, bookId):
        return filter(lambda book: book.id == bookId, self.books)[0]

    def updateBook(self, bookId, author, title, cover = None):
        book = self.getBook(bookId)
        if not self.readOnly:
            book.author = author
            book.title = title
            if cover:
                book.cover = cover

    def removeBook(self, bookId):
        if not self.readOnly:
            self.books.removeIf(PyPredicate(lambda book: book.id == bookId))

    def findBooks(self, searchString):
        return list(filter(lambda book: searchString is None or re.search(searchString.upper(), book.author.upper())\
                            or re.search(searchString.upper(), book.title.upper()), self.books))

    def getAuthors(self):
        return sorted(list(set(list(map(lambda book: book.author, self.books)))), key = lambda author: author.lower())
