"""
Sponge Knowledge Base
Remote API security
"""

from org.openksavi.sponge.remoteapi.server.security import User

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "guest":["example"], "anonymous":["example"]}
# Simple access configuration: role -> event names regexps.
ROLES_TO_SEND_EVENT = { "admin":[".*"], "guest":[".*"], "anonymous":[]}
ROLES_TO_SUBSCRIBE_EVENT = { "admin":[".*"], "guest":[".*"], "anonymous":[".*"]}

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
    # Set up users.
    securityService = remoteApiServer.service.securityService
    securityService.addUser(User("john", "f4f28d85c27f6038bbdd2c8c73c4c2d2ca21350b368431b641999d3f6e1a38a474bae4b6856556532b30fc36a72272be4922ebe3d3b720ee3224b6bb7ced79b4", ["admin"]))
    securityService.addUser(User("joe", "babe834270d043648d757e2db838429a3eedd455cb9cd64b22b8e3914e1b4dd5f98fdd12f7fbd4556b45f82bf79f7d8e66798827a303bdac65eb93da75da9fc3", ["guest"]))

