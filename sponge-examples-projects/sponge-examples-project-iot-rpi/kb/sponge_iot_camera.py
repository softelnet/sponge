"""
Sponge Knowledge base
Camera
"""

from java.lang import System
from java.util.concurrent.locks import ReentrantLock
from org.openksavi.sponge.util.process import ProcessConfiguration
import os

def onInit():
    global CAMERA_LOCK
    CAMERA_LOCK = ReentrantLock(True)

class TakePicture(Action):
    def onConfigure(self):
        self.label = "Take a picture"
        self.description = "Takes a picture using the RPI camera."
        self.argsMeta = []
        self.resultMeta = ResultMeta(BinaryType().withMimeType("image/" + sponge.getVariable("pictureFormat"))).withLabel("Picture")

    def onCall(self):
        CAMERA_LOCK.lock()
        try:
            return sponge.process(createRaspistillBuilder().arguments(
                "--output", "-").outputAsBinary().errorAsException()).run().outputBinary
        finally:
            CAMERA_LOCK.unlock()

class TakePictureAsFile(Action):
    """ Take a picture and save to a file.
    """
    def onCall(self):
        cameraDir = sponge.getProperty("camera.dir")
        if not os.path.exists(cameraDir):
            os.makedirs(cameraDir)

        pictureFileName = "{}/{}.{}".format(cameraDir, str(System.currentTimeMillis()), sponge.getVariable("pictureFormat"))
        CAMERA_LOCK.lock()
        try:
            sponge.process(createRaspistillBuilder().arguments("--output", pictureFileName).errorAsException()).run().waitFor()
        finally:
            CAMERA_LOCK.unlock()

        return pictureFileName

def createRaspistillBuilder():
    return ProcessConfiguration.builder("raspistill", "--width", "500", "--height", "500","--encoding",
                                        sponge.getVariable("pictureFormat"))
