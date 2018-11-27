"""
Sponge Knowledge base
IoT on Raspberry Pi with a GrovePi board
"""

from com.fazecast.jSerialComm import SerialPort
from com.hopding.jrpicam import RPiCamera
from com.hopding.jrpicam.enums import Encoding

from org.apache.commons.mail import SimpleEmail, MultiPartEmail, EmailAttachment, DefaultAuthenticator

import socket
import sys
import os

class SetLcd(Action):
    def onConfigure(self):
        self.displayName = "Set LCD text and color"
        self.description = "Sets LCD properties (display text and color). A null value doesn't change an LCD property."
        self.argsMeta = [
            ArgMeta("text", StringType().maxLength(256).nullable(True).features({"multiline":"true"}))
                .displayName("Text to display").description("The text that will be displayed in the LCD."),
            ArgMeta("color", StringType().maxLength(6).nullable(True).features({"characteristic":"color"}))
                .displayName("LCD color").description("The LCD color.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, text, color):
        if text is not None:
            grovepi.lCD.text = text
        if color is not None:
            rgb = tuple(int(color[i:i+2], 16) for i in (0, 2 ,4))
            grovepi.lCD.setRGB(rgb[0], rgb[1], rgb[2])

# TODO Format - phone number.
class SendSms(Action):
    def onConfigure(self):
        self.displayName = "Send SMS"
        self.description = "Sends a new SMS."
        self.argsMeta = [
            ArgMeta("recipient", StringType().maxLength(16))
                .displayName("Recipient").description("The SMS recipient."),
            ArgMeta("message", StringType().maxLength(160).features({"multiline":"true"}))
                .displayName("Message").description("The SMS message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, recipient, message):
        gsm.modem.sendSms(recipient, message)

class TakePicture(Action):
    imageFormat = "png"
    def onConfigure(self):
        self.displayName = "Take a picture"
        self.description = "Takes a picture using the RPI camera."
        self.argsMeta = []
        self.resultMeta = ResultMeta(BinaryType().mimeType("image/" + TakePicture.imageFormat)).displayName("Picture")
    def onCall(self):
        # TODO Lock?
        return pi.takePictureAsBytes(TakePicture.imageFormat)

class SendNotificationEmail(Action):
    def onConfigure(self):
        self.displayName = "Send a notification email"
        self.description = "Sends a notification email."
        self.argsMeta = [
            ArgMeta("subject", StringType()).displayName("Subject").description("The email subject."),
            ArgMeta("message", StringType().features({"multiline":"true"})).displayName("Message").description("The email message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, subject, message):
        sendNotificationEmail(subject, message)

def setupGsmSeriaPort(serialPort):
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0)
    serialPort.baudRate = 19200
    #serialPort.clearRTS()
    #serialPort.flowControl = 
    #setComPortParameters(19200, newDataBits, newStopBits, newParity)

def createCamera():
    camera = RPiCamera()
    camera.width = 500
    camera.height = 500
    camera.encoding = Encoding.PNG
    
    return camera

def sendNotificationEmail(subject, message, attachmentFile = None):
    email = SimpleEmail() if attachmentFile is None else MultiPartEmail()
    email.setHostName(sponge.getProperty("mail.host"))
    
    username = sponge.getProperty("mail.user", None)
    if username:
        email.setAuthenticator(DefaultAuthenticator(username, sponge.getProperty("mail.password")))
        
    email.setFrom(sponge.getProperty("mail.from"))
    email.addTo(sponge.getProperty("mail.to"))
    email.setSubject(subject)
    email.setMsg(message)
    
    if attachmentFile:
        attachment = EmailAttachment()
        attachment.setPath(attachmentFile)
        attachment.setDisposition(EmailAttachment.ATTACHMENT)
        attachment.setName(os.path.basename(attachmentFile))
        email.attach(attachment)
    
    email.send()

def onStartup():
    # TODO setupGsmSeriaPort(gsm.modem.serialPort)
    #pi.camera = createCamera()
    
    camel.waitForContextFullyStarted()
    
    # Manual start of the REST API (autoStart is turned off) because the REST API server must start after the Camel context has started.
    restApiServer.start()

    # TODO Camera picture attachment
    #sendNotificationEmail("Sponge IoT service started", "Host: {}".format(socket.gethostname())) #, "file.png")
    #sendNotificationEmail("Sponge IoT service started", "Host: {}".format(socket.gethostname()), "pom.xml")

def onShutdown():
    pass
#     try:
#         sendNotificationEmail("Sponge IoT service stopped", "Host: {}".format(socket.gethostname()))
#     except:
#         sponge.logger.warn("Email notification error: {}", sys.exc_info()[1])