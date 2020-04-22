"""
Sponge Knowledge Base
User Management - public
"""

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
