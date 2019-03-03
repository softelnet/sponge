"""
Sponge Knowledge base
SMS
"""

class SendSms(Action):
    def onConfigure(self):
        self.withLabel("Send an SMS").withDescription("Sends a new SMS.")
        self.withArgs([
            StringType("recipient").withFormat("phone").withLabel("Recipient").withDescription("The SMS recipient."),
            StringType("message").withMaxLength(160).withFeatures({"maxLines":5}).withLabel("Message").withDescription("The SMS message.")
        ]).withNoResult()
        self.withFeature("icon", "cellphone-text")
    def onCall(self, recipient, message):
        gsm.sendSms(recipient, message)

class SendNotificationSms(Action):
    def onConfigure(self):
        self.withLabel("Send a notification SMS").withDescription("Sends a notification SMS.")
        self.withArg(
            StringType("message").withMaxLength(160).withFeatures({"maxLines":5})
                .withLabel("Message").withDescription("The SMS message.")
        ).withNoResult()
        self.withFeature("icon", "cellphone-text")
    def onCall(self, message):
        notificationSmsPhone = sponge.getProperty("sms.phone", None)
        if notificationSmsPhone:
            sponge.call("SendSms", [notificationSmsPhone, message])
