"""
Sponge Knowledge base
REST API security
"""

from org.openksavi.sponge.restapi.server.security import User

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "anonymous":["public"], "standard":["public", "account", "service"]}

class RemoteApiCanUseKnowledgeBase(Action):
    def onCall(self, userContext, kbName):
        return restApiServer.canAccessResource(ROLES_TO_KB, userContext, kbName)


def onStartup():
    # Setup users. To hash a password use (on Mac): echo -n username-password | shasum -a 512 | awk '{ print $1 }'
    # Note that the user name must be lower case.
    securityService = restApiServer.service.securityService

    # Sample users have password: password
    securityService.addUser(User("admin@openksavi.org", "25205e6ea66af5e682493b3ed7435e446742fbdba6cce5bd92a599df7668417d01305e8fcdf7a924861adfc3815824b8de8b595ac4ad64881957df20dc58cf2e", ["admin"]))
    securityService.addUser(User("user1@openksavi.org", "eaa4e94ea0817fd4395a042236212957c5ecb764c3a3f7768b1d28f58a54a3253075cca242d8a472e4ab8231c7bc4ae76cec6392d3235cc38e93194a69e557c8", ["standard"]))

