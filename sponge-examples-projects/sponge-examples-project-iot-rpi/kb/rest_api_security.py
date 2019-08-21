"""
Sponge Knowledge base
REST API security
"""

from org.openksavi.sponge.restapi.server.security import User

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "normal":["iot", "grovepi", "email", "sms", "camera", "events"]}
# Simple access configuration: role -> event names regexps.
ROLES_TO_SEND_EVENT = { "admin":[".*"], "normal":[".*"]}
ROLES_TO_SUBSCRIBE_EVENT = { "admin":[".*"], "normal":[".*"]}

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
    # Setup users. To hash a password use (on Mac): echo -n username-password | shasum -a 512 | awk '{ print $1 }'
    # Note that the user name must be lower case.
    securityService = restApiServer.service.securityService

    passwordFile = sponge.engine.configurationManager.getProperty("password.file")
    if not passwordFile:
        raise Exception("Password file property not found")

    with open(passwordFile, "r") as f:
        for username, roleList, password in [line.split(":") for line in f.readlines()]:
            if username.strip():
                roles = [role.strip() for role in roleList.split(",")]
                securityService.addUser(User(username.strip(), password.strip(), roles))

