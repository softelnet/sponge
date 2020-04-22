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
              BinaryType("image").withLabel("Doodle").withMimeType("image/png")
                     .withFeatures({"characteristic":"drawing", "width":300, "height":250, "background":"FFFFFF", "color":"000000", "strokeWidth":2})
        )
        self.withResult(StringType().withLabel("Status"))
        self.withFeatures({"icon":"brush"})
    def onCall(self, image):
        if not sponge.getVariable("demo.readOnly", False):
            filename = str(System.currentTimeMillis()) + ".png"
            SpongeUtils.writeByteArrayToFile(image, sponge.getProperty("doodlesDir") + "/" + filename)
            return "Uploaded as " + filename
        else:
            return "Uploading disabled in the read only mode"

class ListDoodles(Action):
    def onConfigure(self):
        self.withLabel("List doodles").withDescription("Returns a list of doodle filenames").withFeatures({"visible":False})
        self.withNoArgs().withResult(ListType(StringType()).withLabel("Doodles"))
    def onCall(self):
        dir = sponge.getProperty("doodlesDir")
        doodles = [f for f in listdir(dir) if isfile(join(dir, f)) and f.endswith(".png")] if isdir(dir) else []
        return sorted(doodles, reverse=True)

class ViewDoodle(Action):
    def onConfigure(self):
        self.withLabel("View a doodle").withDescription("Views a doodle")
        self.withArg(StringType("image").withLabel("Doodle name").withProvided(ProvidedMeta().withValue().withValueSet().withOverwrite()))
        self.withResult(BinaryType().withAnnotated().withMimeType("image/png").withLabel("Doodle image"))
        self.withFeature("icon", "drawing")
    def onCall(self, name):
        return AnnotatedValue(SpongeUtils.readFileToByteArray(sponge.getProperty("doodlesDir") + "/" + name)).withFeatures({"filename":"doodle_" + name})
    def onProvideArgs(self, context):
        if "image" in context.provide:
            doodles = sponge.call("ListDoodles")
            context.provided["image"] = ProvidedValue().withValue(doodles[0] if doodles else None).withValueSet(doodles)

def onStartup():
    sponge.logger.info(str(sponge.call("ListDoodles")))
