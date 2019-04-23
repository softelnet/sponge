"""
Sponge Knowledge base
Account management
"""

from org.apache.commons.codec.digest import DigestUtils
from org.openksavi.sponge.restapi.server.security import User

class Login(Action):
    def onConfigure(self):
        self.withLabel("Log in")
        self.withArgs([
            StringType("email").withFormat("email").withLabel("Email").withDescription("The user email.").withFeature("intent", "username"),
            StringType("password").withLabel("Password").withDescription("The password.").withFeature("obscure", True),
        ]).withNoResult().withFeatures({"intent":"login", "callLabel":"Log in"})
        self.withFeature("icon", "login")
    def onCall(self, email, password):
        pass

class SignUp(Action):
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
