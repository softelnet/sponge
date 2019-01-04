"""
Sponge Knowledge base
Digits recognition common library 
"""

def createImageArgMeta():
    return ArgMeta("image", BinaryType().mimeType("image/png")
                   .features({"characteristic":"drawing", "width":28, "height":28, "background":"000000", "color":"FFFFFF", "strokeWidth":1.5}))\
                   .displayName("Image of a digit")
