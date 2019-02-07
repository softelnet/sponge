"""
Sponge Knowledge base
SMS
"""

class SendSms(Action):
    def onConfigure(self):
        self.label = "Send an SMS"
        self.description = "Sends a new SMS."
        self.argsMeta = [
            ArgMeta("recipient", StringType().withFormat("phone"))
                .withLabel("Recipient").withDescription("The SMS recipient."),
            ArgMeta("message", StringType().withMaxLength(160).withFeatures({"maxLines":5}))
                .withLabel("Message").withDescription("The SMS message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, recipient, message):
        gsm.sendSms(recipient, message)

class SendNotificationSms(Action):
    def onConfigure(self):
        self.label = "Send a notification SMS"
        self.description = "Sends a notification SMS."
        self.argsMeta = [
            ArgMeta("message", StringType().withMaxLength(160).withFeatures({"maxLines":5}))
                .withLabel("Message").withDescription("The SMS message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, message):
        notificationSmsPhone = sponge.getProperty("sms.phone", None)
        if notificationSmsPhone:
            sponge.call("SendSms", [notificationSmsPhone, message])
