"""
Sponge Knowledge base
Demo - record context actions
"""

class RecordWithContextActions(Action):
    def onConfigure(self):
        self.withLabel("Record argument with context actions").withArg(
            RecordType("book").withLabel("Book").withFields([
                StringType("author").withLabel("Author"),
                StringType("title").withLabel("Title"),
            ]).withProvided(ProvidedMeta().withValue()).withFeature("contextActions", [
                "RecordWithContextActionsAction1(author)", "RecordWithContextActionsAction2(title)"
            ])
        ).withNoResult()
    def onCall(self, book):
        pass
    def onProvideArgs(self, context):
        if "book" in context.names:
            context.provided["book"] = ProvidedValue().withValue({})

class RecordWithContextActionsAction1(Action):
    def onConfigure(self):
        self.withLabel("Add author comment").withArgs([
            StringType("author").withLabel("Author").withFeature("visible", False),
            StringType("comment").withLabel("Comment"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, author, comment):
        return "Added '" + comment + "' comment to author '" + author + "'"

class RecordWithContextActionsAction2(Action):
    def onConfigure(self):
        self.withLabel("Add title comment").withArgs([
            StringType("title").withLabel("Title").withFeature("visible", False),
            StringType("comment").withLabel("Comment"),
        ]).withResult(StringType())
        self.withFeatures({"visible":False, "icon":"tortoise"})
    def onCall(self, title, comment):
        return "Added '" + comment + "' comment to title '" + title + "'"
