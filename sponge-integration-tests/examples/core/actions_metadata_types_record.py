"""
Sponge Knowledge base
Action metadata Record type
"""

def createBookType():
    return RecordType("Book", [
                RecordTypeField("id", IntegerType().withNullable()).withLabel("Identifier"),
                RecordTypeField("author", StringType()).withLabel("Author"),
                RecordTypeField("title", StringType()).withLabel("Title"),
            ])

BOOK = {"id":1, "author":"James Joyce", "title":"Ulysses"}

class RecordAsResultAction(Action):
    def onConfigure(self):
        self.withArg(ArgMeta("bookId", IntegerType())).withResult(ResultMeta(createBookType().withNullable()))
    def onCall(self, bookId):
        global BOOK
        return BOOK if bookId == BOOK["id"] else None

class RecordAsArgAction(Action):
    def onConfigure(self):
        self.withArg(ArgMeta("book", createBookType())).withNoResult()
    def onCall(self, book):
        global BOOK
        BOOK = {"id":1, "author":book["author"], "title":book["title"]}

