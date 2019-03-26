"""
Sponge Knowledge base
REST API security
"""

from org.openksavi.sponge.restapi.server.security import User

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "anonymous":["account"], "standard":["account", "service"]}

class RestApiCanUseKnowledgeBase(Action):
    def onCall(self, user, kbName):
        return restApiServer.canUseKnowledgeBase(ROLES_TO_KB, user, kbName)


def onStartup():
    # Set up users. To hash a password use (on Mac): echo -n <username><password> | shasum -a 256 | awk '{ print $1 }'
    # Note that the user name must be lower case.
    securityService = restApiServer.service.securityService
    
    # Sample users have password: password
    securityService.addUser(User("admin@openksavi.org", "03d8458b25bda0544ff6e73c71f3cd842ef74866de107e69a3d2d0fce4713798", ["admin"]))
    securityService.addUser(User("user1@openksavi.org", "2a0c38aec407d03b27a89f9062332ad9a6b65df48669385ce7df01f696c3dcbd", ["standard"]))

