"""
Sponge Knowledge Base
Remote API security
"""

def configureAccessService():
    # Configure the RoleBasedAccessService.

    # Simple access configuration: role -> knowledge base names regexps.
    remoteApiServer.accessService.addRolesToKb({ "ROLE_ADMIN":[".*"], "ROLE_ANONYMOUS":["boot", "python"]})

    # Simple access configuration: role -> event names regexps.
    remoteApiServer.accessService.addRolesToSendEvent({ "ROLE_ADMIN":[".*"], "ROLE_ANONYMOUS":[]})
    remoteApiServer.accessService.addRolesToSubscribeEvent({ "ROLE_ADMIN":[".*"], "ROLE_ANONYMOUS":[".*"]})

def onStartup():
    # Configure the access service on startup.
    configureAccessService()

def onAfterReload():
    # Reconfigure the access service after each reload.
    configureAccessService()