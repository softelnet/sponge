"""
Sponge Knowledge Base
Start
"""

class HelloWorldAction(Action):
    def onConfigure(self):
        self.withLabel("Hello world").withDescription("Returns a greeting text.")
        self.withArg(StringType("name").withLabel("Your name").withDescription("Type your name."))
        self.withResult(StringType().withLabel("Greeting").withDescription("The greeting text."))
    def onCall(self, name):
        return u"Hello World! Hello {}!".format(name)

class DrawDoodle(Action):
    def onConfigure(self):
        self.withLabel("Draw a doodle").withDescription("Shows a canvas to draw a doodle")
        self.withArg(BinaryType("image").withLabel("Doodle").withMimeType("image/png")
                     .withFeatures({"characteristic":"drawing", "width":300, "height":250, "background":"FFFFFF", "color":"000000", "strokeWidth":2}))
        self.withNoResult().withFeatures({"icon":"brush", "callLabel":"OK", "showClear":True, "showCancel":True})
    def onCall(self, image):
        pass
