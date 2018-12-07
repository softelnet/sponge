"""
Sponge Knowledge base
IoT on Raspberry Pi with a GrovePi board
"""

from com.hopding.jrpicam import RPiCamera
from com.hopding.jrpicam.enums import Encoding
from org.apache.commons.mail import SimpleEmail, MultiPartEmail, EmailAttachment, DefaultAuthenticator
from org.openksavi.sponge.gsmmodem import GsmModemUtils
from org.openksavi.sponge.util.process import ProcessConfiguration
from java.lang import System
from java.util.concurrent.locks import ReentrantLock

import socket
import sys
import os

SERVICE_NAME = sponge.getProperty("service.name", "Sponge IoT service")
NOTIFICATION_SMS_PHONE = sponge.getProperty("service.notification.sms.phone", None)
PICTURE_FORMAT = "jpg"

class SetLcd(Action):
    def onConfigure(self):
        self.displayName = "Set LCD text and color"
        self.description = "Sets LCD properties (display text and color). A null value doesn't change an LCD property."
        self.argsMeta = [
            ArgMeta("text", StringType().maxLength(256).nullable(True).features({"maxLines":5}))
                .displayName("Text to display").description("The text that will be displayed in the LCD."),
            ArgMeta("color", StringType().maxLength(6).nullable(True).features({"characteristic":"color"}))
                .displayName("LCD color").description("The LCD color."),
            ArgMeta("clearText", BooleanType().nullable(True).defaultValue(False))
                .displayName("Clear text").description("The text the LCD will be cleared.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, text, color, clearText = None):
        text = "" if clearText else (text if text is not None else sponge.getVariable("lcdText", ""))
            
        if color is not None:
            rgb = tuple(int(color[i:i+2], 16) for i in (0, 2 ,4))
            grovepi.device.LCD.setRGB(rgb[0], rgb[1], rgb[2])
            
        grovepi.device.LCD.text = text
        sponge.setVariable("lcdText", text)

class GetLcdText(Action):
    def onConfigure(self):
        self.displayName = "Get LCD text"
        self.description = "Returns the LCD text."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType().features({"maxLines":5})).displayName("LCD Text")
    def onCall(self):
        return sponge.getVariable("lcdText")

# TODO Format - phone number.
class SendSms(Action):
    def onConfigure(self):
        self.displayName = "Send SMS"
        self.description = "Sends a new SMS."
        self.argsMeta = [
            ArgMeta("recipient", StringType().format("phone"))
                .displayName("Recipient").description("The SMS recipient."),
            ArgMeta("message", StringType().maxLength(160).features({"maxLines":5}))
                .displayName("Message").description("The SMS message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, recipient, message):
        gsm.sendSms(recipient, message)

class TakePicture(Action):
    def onConfigure(self):
        self.displayName = "Take picture"
        self.description = "Takes a picture using the RPI camera."
        self.argsMeta = []
        self.resultMeta = ResultMeta(BinaryType().mimeType("image/" + PICTURE_FORMAT)).displayName("Picture")
    def onCall(self):
        CAMERA_LOCK.lock()
        try:
            return sponge.process(createRaspistillBuilder().arguments(
                "--output", "-").outputAsBinary().errorAsException()).run().outputBinary
        finally:
            CAMERA_LOCK.unlock()

class SendNotificationEmail(Action):
    def onConfigure(self):
        self.displayName = "Send notification email"
        self.description = "Sends a notification email."
        self.argsMeta = [
            ArgMeta("subject", StringType()).displayName("Subject").description("The email subject."),
            ArgMeta("message", StringType().features({"maxLines":5})).displayName(
                "Message").description("The email message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, subject, message):
        sendNotificationEmail(subject, message)

class SendNotificationSms(Action):
    def onConfigure(self):
        self.displayName = "Send notification SMS"
        self.description = "Sends a notification SMS."
        self.argsMeta = [
            ArgMeta("message", StringType().maxLength(160).features({"maxLines":5}))
                .displayName("Message").description("The SMS message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, message):
        if NOTIFICATION_SMS_PHONE:
            sponge.call("SendSms", NOTIFICATION_SMS_PHONE, message)

def sendNotificationEmail(subject, message, attachmentFiles = []):
    email = SimpleEmail() if len(attachmentFiles) == 0 else MultiPartEmail()
    email.setHostName(sponge.getProperty("mail.host"))
    
    username = sponge.getProperty("mail.user", None)
    if username:
        email.setAuthenticator(DefaultAuthenticator(username, sponge.getProperty("mail.password")))
        
    email.setFrom(sponge.getProperty("mail.from"))
    email.addTo(sponge.getProperty("mail.to"))
    email.setSubject(subject)
    email.setMsg(message)
    
    for attachmentFile in attachmentFiles:
        attachment = EmailAttachment()
        attachment.setPath(attachmentFile)
        attachment.setDisposition(EmailAttachment.ATTACHMENT)
        attachment.setName(os.path.basename(attachmentFile))
        email.attach(attachment)
    
    email.send()
    
def createRaspistillBuilder():
    return ProcessConfiguration.builder("raspistill", "--width", "500", "--height", "500","--encoding", PICTURE_FORMAT)

def takePictureAsFile():
    """ Take a picture and save to a file.
    """
    cameraDir = sponge.getProperty("camera.dir")
    pictureFileName = cameraDir + "/" + str(System.currentTimeMillis()) + "." + PICTURE_FORMAT
        
    if not os.path.exists(cameraDir):
        os.makedirs(cameraDir)

    CAMERA_LOCK.lock()
    try:
        sponge.process(createRaspistillBuilder().arguments("--output", pictureFileName).errorAsException()).run().waitFor()
    finally:
        CAMERA_LOCK.unlock()
        
    return pictureFileName

class StartNotificationTrigger(Trigger):
    def onConfigure(self):
        self.event = "notificationStart"
    def onRun(self, event):
        # Send an email.
        sendNotificationEmail("{} started".format(SERVICE_NAME), "Host: {}".format(socket.gethostname()),
                    [takePictureAsFile()])
        # Send an SMS.
        gsm.sendSms(NOTIFICATION_SMS_PHONE, "{} started".format(SERVICE_NAME))

def onStartup():
    global CAMERA_LOCK
    CAMERA_LOCK = ReentrantLock(True)
    
    # Set LCD.
    sponge.call("SetLcd", "Starting Sponge...", "006030")

    # Manual start of the REST API (autoStart is turned off) because the REST API server must start after the Camel context has started.
    camel.waitForContextFullyStarted()
    restApiServer.start()

    # Set LCD.
    sponge.call("SetLcd", "Sponge running", "00f767")
    
    # Sent start notification event.
    sponge.event("notificationStart").send()

def onShutdown():
     try:
         sponge.call("SetLcd", "", "000000")
     except:
         sponge.logger.warn("Shutdown error: {}", sys.exc_info()[1])

def onAfterReload():
    pass
