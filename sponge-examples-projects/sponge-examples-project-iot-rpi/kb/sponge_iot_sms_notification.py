"""
Sponge Knowledge Base
SMS
"""

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
