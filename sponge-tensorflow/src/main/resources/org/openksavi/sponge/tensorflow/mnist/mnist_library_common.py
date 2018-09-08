"""
Sponge Knowledge base
MNIST common library 
"""

def createImageArgMeta():
    return ArgMeta("image", BinaryType().mimeType("image/png")
                   .features({"source":"drawing", "width":28, "height":28, "background":"000000", "color":"FFFFFF", "strokeWidth":1.5}))\
                   .displayName("Image of a digit")
