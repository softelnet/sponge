"""
Sponge Knowledge base
REST API security
"""

from org.openksavi.sponge.restapi.security import User

# Simple access configuration role -> knowledge base name regexp.
ROLES_TO_KB = { "admin":".*", "guest":"example"}

class RestApiCanUseKnowledgeBase(Action):
    def onCall(self, user, kbName):
        return restApiCanUseKnowledgeBase(ROLES_TO_KB, user, kbName)


def onStartup():
    # Set up users.
    securityService = restApi.service.securityService
    
    # Set up users. To hash a password use (on Mac): echo -n <username><password> | shasum -a 256 | awk '{ print $1 }'
    # Note that the user name must be lower case.
    securityService = restApi.service.securityService
    securityService.addUser(User("john", "4ae0aa2783d6e8a939b0a3ce8146400001ef6b9840958aea136b416c58a2f9b8", ["admin"]))
    securityService.addUser(User("joe", "e52abe94e1e06f794a3654e02bfbe71565025ea6ce2898d1ad459182f3bc147b", ["guest"]))

