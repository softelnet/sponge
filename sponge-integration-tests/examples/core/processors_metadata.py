"""
Sponge Knowledge base
Processors metadata
"""

from org.openksavi.sponge.examples import PowerEchoMetadataAction

class UpperEchoAction(Action):
    def onConfigure(self):
        self.meta = {"visibility":False}
    def onCall(self, text):
    	return None

def onLoad():
    EPS.enableJava(PowerEchoMetadataAction)
