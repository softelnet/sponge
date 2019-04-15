"""
Sponge Knowledge base
Account management
"""

class Logout(Action):
    def onConfigure(self):
        self.withLabel("Log out").withNoArgs().withNoResult().withFeature("intent", "logout").withFeature("icon", "logout")
    def onCall(self):
        pass
