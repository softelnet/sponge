"""
Sponge Knowledge Base
User Management - public
"""

class Login(Action):
    def onConfigure(self):
        self.withLabel("Log in")
        self.withArgs([
            StringType("email").withFormat("email").withLabel("Email").withDescription("The user email.").withFeature("intent", "username"),
            StringType("password").withLabel("Password").withDescription("The password.").withFeature("intent", "password").withFeature("obscure", True),
            BooleanType("savePassword").withLabel("Save password").withDescription("Save the password.").withFeature("intent", "savePassword").withOptional(),
        ]).withNoResult().withFeatures({"intent":"login", "icon":"login", "callLabel":"Log in"})
    def onCall(self, email, password, savePassword = None):
        pass
