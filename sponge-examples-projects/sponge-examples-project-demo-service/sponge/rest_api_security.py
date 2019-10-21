"""
Sponge Knowledge base
REST API security
"""

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "anonymous":["demo", "digits", "demoForms.*"]}
# Simple access configuration: role -> event names regexps.
ROLES_TO_SEND_EVENT = { "admin":[".*"], "anonymous":[]}
ROLES_TO_SUBSCRIBE_EVENT = { "admin":[".*"], "anonymous":["notification.*"]}

class RemoteApiCanUseKnowledgeBase(Action):
    def onCall(self, userContext, kbName):
        return restApiServer.canAccessResource(ROLES_TO_KB, userContext, kbName)

class RemoteApiCanSendEvent(Action):
    def onCall(self, userContext, eventName):
        return restApiServer.canAccessResource(ROLES_TO_SEND_EVENT, userContext, eventName)

class RemoteApiCanSubscribeEvent(Action):
    def onCall(self, userContext, eventName):
        return restApiServer.canAccessResource(ROLES_TO_SUBSCRIBE_EVENT, userContext, eventName)

def onStartup():
    # Load users from a password file.
    restApiServer.service.securityService.loadUsers()
