"""
Sponge Knowledge Base
User Management - Sign Up with an Email
"""

from org.openksavi.sponge.restapi.server.security import User

class SignUpWithEmail(Action):
    def onConfigure(self):
        self.withLabel("Sign up")
        self.withArgs([
            StringType("email").withFormat("email").withLabel("Email").withDescription("The user email."),
            StringType("firstName").withLabel("First name").withDescription("The user first name."),
            StringType("lastName").withLabel("Last name").withDescription("The user last name."),
            StringType("password").withMinLength(8).withLabel("Password").withDescription(
                "The password.").withFeature("obscure", True),
            StringType("passwordConfirmation").withMinLength(8).withLabel("Password confirmation").withDescription(
                "The password confirmation.").withFeature("obscure", True),
        ]).withNoResult().withFeatures({"intent":"signUp", "callLabel":"Sign up"})
        self.withFeature("icon", "face")
    def onCall(self, email, firstName, lastName, password, passwordConfirmation):
        if password != passwordConfirmation:
            raise Exception("Passwords don't match")
        encryptedPassword = restApiServer.service.securityService.hashPassword(email, password)
        restApiServer.service.securityService.addUser(User(email, encryptedPassword, ["standard"]))
