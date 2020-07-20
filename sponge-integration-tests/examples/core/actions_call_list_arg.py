"""
Sponge Knowledge Base
Action call list arg
"""

class ListArgAction(Action):
    def onCall(self, arg1, arg2, arg3 = []):
        sponge.logger.debug("Arg1={}, arg2={}, arg3={}", arg1, arg2, str(arg3))
        return arg3
