"""
Sponge Knowledge base
SMS
"""

class SendSms(Action):
    def onConfigure(self):
        self.displayName = "Send an SMS"
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

class SendNotificationSms(Action):
    def onConfigure(self):
        self.displayName = "Send a notification SMS"
        self.description = "Sends a notification SMS."
        self.argsMeta = [
            ArgMeta("message", StringType().maxLength(160).features({"maxLines":5}))
                .displayName("Message").description("The SMS message.")
        ]
        self.resultMeta = ResultMeta(VoidType())
    def onCall(self, message):
        notificationSmsPhone = sponge.getProperty("sms.phone", None)
        if notificationSmsPhone:
            sponge.call("SendSms", [notificationSmsPhone, message])
