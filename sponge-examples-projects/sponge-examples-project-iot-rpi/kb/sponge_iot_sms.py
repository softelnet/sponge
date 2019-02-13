"""
Sponge Knowledge base
SMS
"""

class SendSms(Action):
    def onConfigure(self):
        self.withLabel("Send an SMS").withDescription("Sends a new SMS.")
        self.withArgs([
            ArgMeta("recipient", StringType().withFormat("phone"))
                .withLabel("Recipient").withDescription("The SMS recipient."),
            ArgMeta("message", StringType().withMaxLength(160).withFeatures({"maxLines":5}))
                .withLabel("Message").withDescription("The SMS message.")
        ]).withNoResult()
    def onCall(self, recipient, message):
        gsm.sendSms(recipient, message)

class SendNotificationSms(Action):
    def onConfigure(self):
        self.withLabel("Send a notification SMS").withDescription("Sends a notification SMS.")
        self.withArg(
            ArgMeta("message", StringType().withMaxLength(160).withFeatures({"maxLines":5}))
                .withLabel("Message").withDescription("The SMS message.")
        ).withNoResult()
    def onCall(self, message):
        notificationSmsPhone = sponge.getProperty("sms.phone", None)
        if notificationSmsPhone:
            sponge.call("SendSms", [notificationSmsPhone, message])
