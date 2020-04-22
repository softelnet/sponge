"""
Sponge Knowledge Base
User management - Logged
"""

class Logout(Action):
    def onConfigure(self):
        self.withLabel("Log out").withNoArgs().withNoResult().withFeature("intent", "logout").withFeature("icon", "logout")
    def onCall(self):
        pass
