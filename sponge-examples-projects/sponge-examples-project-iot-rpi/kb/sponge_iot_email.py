"""
Sponge Knowledge base
Email
"""

from org.apache.commons.mail import SimpleEmail, MultiPartEmail, EmailAttachment, DefaultAuthenticator
import os

class SendNotificationEmail(Action):
    def onConfigure(self):
        self.withLabel("Send a notification email").withDescription("Sends a notification email.")
        self.withArgs([
            StringType("subject").withLabel("Subject").withDescription("The email subject."),
            StringType("message").withFeatures({"maxLines":5}).withLabel("Message").withDescription("The email message.")
            # The attachmentFiles attribute won't be visible in the UI.
        ])
        self.withNoResult()
    def onCall(self, subject, message, attachmentFiles = []):
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
