"""
Sponge Knowledge Base
Action metadata Record type
"""

def createBookType(name):
    return RecordType(name, [
                IntegerType("id").withNullable().withLabel("Identifier"),
                StringType("author").withLabel("Author"),
                StringType("title").withLabel("Title")
            ])

BOOK = {"id":1, "author":"James Joyce", "title":"Ulysses"}

class RecordAsResultAction(Action):
    def onConfigure(self):
        self.withArg(IntegerType("bookId")).withResult(createBookType("book").withNullable())
    def onCall(self, bookId):
        global BOOK
        return BOOK if bookId == BOOK["id"] else None

class RecordAsArgAction(Action):
    def onConfigure(self):
        self.withArg(createBookType("book")).withNoResult()
    def onCall(self, book):
        global BOOK
        BOOK = {"id":1, "author":book["author"], "title":book["title"]}

