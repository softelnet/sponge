"""
Sponge Knowledge base
REST API security
"""

from org.openksavi.sponge.restapi.security import Role, User

# Simple access configuration role -> knowledge base name regexp.
ROLES_TO_KB = { Role.ADMIN:".*", Role.GUEST:"example"}

class RestApiCanUseKnowledgeBase(Action):
    def onCall(self, user, kbName):
        return restApiCanUseKnowledgeBase(ROLES_TO_KB, user, kbName)


def onStartup():
    # Set up users.
    securityService = restApi.service.securityService
    
    securityService.addUser(User("john", "ce8d843f79bb3d3372aa91f675fb21f5cc9926d1bc9117da6018b7df10496769", [Role.ADMIN]))
    securityService.addUser(User("joe", "ce8d843f79bb3d3372aa91f675fb21f5cc9926d1bc9117da6018b7df10496769", [Role.GUEST]))

