"""
Sponge Knowledge Base
Remote API security
"""

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "anonymous":[".*"]}
# Simple access configuration: role -> event names regexps.
ROLES_TO_SEND_EVENT = { "admin":[".*"], "anonymous":[]}
ROLES_TO_SUBSCRIBE_EVENT = { "admin":[".*"], "anonymous":[".*"]}

class RemoteApiCanUseKnowledgeBase(Action):
    def onCall(self, userContext, kbName):
        return remoteApiServer.canAccessResource(ROLES_TO_KB, userContext, kbName)

class RemoteApiCanSendEvent(Action):
    def onCall(self, userContext, eventName):
        return remoteApiServer.canAccessResource(ROLES_TO_SEND_EVENT, userContext, eventName)

class RemoteApiCanSubscribeEvent(Action):
    def onCall(self, userContext, eventName):
        return remoteApiServer.canAccessResource(ROLES_TO_SUBSCRIBE_EVENT, userContext, eventName)

def onStartup():
    # Load users from a password file.
    remoteApiServer.service.securityService.loadUsers()
