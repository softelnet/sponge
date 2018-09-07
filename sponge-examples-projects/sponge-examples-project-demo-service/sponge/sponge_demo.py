"""
Sponge Knowledge base
Demo
"""

class DrawDoodle(Action):
    def onConfigure(self):
        self.displayName = "Draw a doodle"
        self.description = "Shows a canvas to draw a doodle"
        self.argsMeta = [ArgMeta("image", BinaryType().mimeType("image/png")
                   .features({"source":"drawing", "width":200, "height":250, "background":"FFFFFF", "color":"000000", "strokeWidth":5}))\
                   .displayName("Doodle")
        ]
        self.resultMeta = ResultMeta(StringType()).displayName("Result")
    def onCall(self, image):
        return "OK"
