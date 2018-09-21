"""
Sponge Knowledge base
Demo Plus
"""

from os import listdir
from os.path import isfile, join
from java.lang import System

class DrawAndUploadDoodle(Action):
    def onConfigure(self):
        self.displayName = "Draw and upload a doodle"
        self.description = "Shows a canvas to draw a doodle and uploads it to the server"
        self.argsMeta = [ArgMeta("image", BinaryType().mimeType("image/png")
                   .features({"source":"drawing", "width":300, "height":250, "background":"FFFFFF", "color":"000000", "strokeWidth":5}))\
                   .displayName("Doodle")
        ]
        self.resultMeta = ResultMeta(StringType()).displayName("Status")
    def onCall(self, image):
        fileName = str(System.currentTimeMillis()) + ".png"
        SpongeUtils.writeByteArrayToFile(image, sponge.getProperty("doodlesDir") + "/" + fileName)
        return "Uploaded as " + fileName

class ListDoodles(Action):
    def onConfigure(self):
        self.displayName = "List doodles"
        self.description = "Returns a list of doodle file names"
        self.features = {"visible":False}
        self.argsMeta = []
        self.resultMeta = ResultMeta(ListType(StringType())).displayName("Doodles")
    def onCall(self):
        dir = sponge.getProperty("doodlesDir")
        return [f for f in listdir(dir) if isfile(join(dir, f)) and f.endswith(".png")]

class ViewDoodle(Action):
    def onConfigure(self):
        self.displayName = "View a doodle"
        self.description = "Views a doodle"
        self.argsMeta = [ArgMeta("image", ActionType("ListDoodles")).displayName("Doodle name")]
        self.resultMeta = ResultMeta(BinaryType().mimeType("image/png")).displayName("Doodle image")
    def onCall(self, name):
        return SpongeUtils.readFileToByteArray(sponge.getProperty("doodlesDir") + "/" + name)

def onStartup():
    print(str(sponge.call("ListDoodles")))
