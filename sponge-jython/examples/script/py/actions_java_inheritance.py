"""
Sponge Knowledge base
Actions - Java inheritance
"""

from org.openksavi.sponge.examples import PowerEchoAction

class ExtendedFromAction(PowerEchoAction):
    def onCall(self, value, text):
    	return [value + 10, text.lower()]
