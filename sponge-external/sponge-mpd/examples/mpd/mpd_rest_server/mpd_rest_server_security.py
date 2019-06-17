"""
Sponge Knowledge base
MPD / REST API
"""

from org.openksavi.sponge.restapi.server.security import User

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "guest":["mpd"], "anonymous":["mpd"]}

class RemoteApiCanUseKnowledgeBase(Action):
    def onCall(self, userContext, kbName):
        return restApiServer.canAccessResource(ROLES_TO_KB, userContext, kbName)

def onStartup():
    # Set up users.
    securityService = restApiServer.service.securityService

    # Set up users. To hash a password use (on Mac): echo -n username-password | shasum -a 512 | awk '{ print $1 }'
    # Note that the user name must be lower case.
    securityService = restApiServer.service.securityService
    securityService.addUser(User("john", "f4f28d85c27f6038bbdd2c8c73c4c2d2ca21350b368431b641999d3f6e1a38a474bae4b6856556532b30fc36a72272be4922ebe3d3b720ee3224b6bb7ced79b4", ["admin"]))
    securityService.addUser(User("joe", "babe834270d043648d757e2db838429a3eedd455cb9cd64b22b8e3914e1b4dd5f98fdd12f7fbd4556b45f82bf79f7d8e66798827a303bdac65eb93da75da9fc3", ["guest"]))

