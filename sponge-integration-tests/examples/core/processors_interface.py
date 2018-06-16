"""
Sponge Knowledge base
A processor implementing an additional Java interface
"""

from org.openksavi.sponge.integration.tests.core import TestActionVisibiliy

class EdvancedMetaAction(Action, TestActionVisibiliy):
    def onCall(self, args):
        return args[0].upper()
    def isVisible(self, context):
        return context == "day"
