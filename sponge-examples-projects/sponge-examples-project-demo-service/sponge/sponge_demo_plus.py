"""
Sponge Knowledge base
Demo Plus
"""

from java.lang import System
from os import listdir
from os.path import isfile, join, isdir

class DrawAndUploadDoodle(Action):
    def onConfigure(self):
        self.withLabel("Draw and upload a doodle").withDescription("Shows a canvas to draw a doodle and uploads it to the server")
        self.withArg(
            ArgMeta("image", BinaryType().withMimeType("image/png")
                   .withFeatures({"characteristic":"drawing", "width":300, "height":250, "background":"FFFFFF", "color":"000000", "strokeWidth":5}))\
                   .withLabel("Doodle")
        )
        self.withResult(ResultMeta(StringType()).withLabel("Status"))
    def onCall(self, image):
        fileName = str(System.currentTimeMillis()) + ".png"
        SpongeUtils.writeByteArrayToFile(image, sponge.getProperty("doodlesDir") + "/" + fileName)
        return "Uploaded as " + fileName

class ListDoodles(Action):
    def onConfigure(self):
        self.withLabel("List doodles").withDescription("Returns a list of doodle file names").withFeatures({"visible":False})
        self.withNoArgs().withResult(ResultMeta(ListType(StringType())).withLabel("Doodles"))
    def onCall(self):
        dir = sponge.getProperty("doodlesDir")
        return [f for f in listdir(dir) if isfile(join(dir, f)) and f.endswith(".png")] if isdir(dir) else []

class ViewDoodle(Action):
    def onConfigure(self):
        self.withLabel("View a doodle").withDescription("Views a doodle")
        self.withArg(ArgMeta("image", StringType()).withLabel("Doodle name").withProvided(ArgProvidedMeta().withValue().withValueSet()))
        self.withResult(ResultMeta(AnnotatedType(BinaryType().withMimeType("image/png"))).withLabel("Doodle image"))
    def onCall(self, name):
        return AnnotatedValue(SpongeUtils.readFileToByteArray(sponge.getProperty("doodlesDir") + "/" + name)).withFeatures({"filename":"doodle_" + name})
    def onProvideArgs(self, names, current, provided):
        if "image" in names:
            doodles = sponge.call("ListDoodles")
            provided["image"] = ArgProvidedValue().withValue(doodles[len(doodles)-1] if doodles else None).withValueSet(doodles)

def onStartup():
    sponge.logger.info(str(sponge.call("ListDoodles")))
