"""
Sponge Knowledge base
Demo Plus
"""

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

def onStartup():
    print(str(sponge.call("ListDoodles")))
