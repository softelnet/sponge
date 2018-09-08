"""
Sponge Knowledge base
Demo Admin
"""

from os import listdir
from os.path import isfile, join

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
