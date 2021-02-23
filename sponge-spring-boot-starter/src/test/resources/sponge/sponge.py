"""
Sponge Knowledge Base
"""

class UpperCase(Action):
    def onCall(self, text):
        return text.upper()