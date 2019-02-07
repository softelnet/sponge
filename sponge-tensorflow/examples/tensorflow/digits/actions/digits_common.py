"""
Sponge Knowledge base
Digits recognition common library 
"""

def createImageArgMeta():
    return ArgMeta("image", BinaryType().withMimeType("image/png")
                   .withFeatures({"characteristic":"drawing", "width":28, "height":28, "background":"000000", "color":"FFFFFF", "strokeWidth":1.5}))\
                   .withLabel("Image of a digit")
