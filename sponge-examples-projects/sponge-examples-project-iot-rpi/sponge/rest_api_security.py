"""
Sponge Knowledge base
REST API security
"""

from org.openksavi.sponge.restapi.server.security import User

# Simple access configuration: role -> knowledge base names regexps.
ROLES_TO_KB = { "admin":[".*"], "normal":["iot"]}

class RestApiCanUseKnowledgeBase(Action):
    def onCall(self, user, kbName):
        return restApiServer.canUseKnowledgeBase(ROLES_TO_KB, user, kbName)


def onStartup():
    # Set up users. To hash a password use (on Mac): echo -n <username><password> | shasum -a 256 | awk '{ print $1 }'
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

