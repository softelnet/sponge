"""
Sponge Knowledge base
Action using registered types
"""

def onBeforeLoad():
    sponge.addType("Author", lambda: RecordType([
                StringType("firstName").withLabel("First name"),
                StringType("surname").withLabel("Surname")
            ]))
    sponge.addType("Book", lambda: RecordType([
                sponge.getType("Author", "author").withLabel("Author"),
                StringType("title").withLabel("Title")
            ]))

class GetBookAuthorSurname(Action):
    def onConfigure(self):
        self.withLabel("Get a book author").withArg(sponge.getType("Book").withName("book")).withResult(sponge.getType("Author"))
    def onCall(self, book):
    	return book["author"]["surname"]
