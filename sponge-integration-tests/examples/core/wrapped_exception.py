"""
Sponge Knowledge Base
Wrapped exception
"""

from java.lang import String

class WrappedExceptionAction(Action):
    def onCall(self):
        # Force an exception from Java.
        String("a").charAt(10)