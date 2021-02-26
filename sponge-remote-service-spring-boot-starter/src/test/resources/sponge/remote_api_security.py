"""
Sponge Knowledge Base
Remote API security
"""

def configureAccessService():
    # Configure the RoleBasedAccessService.

    # Simple access configuration: role -> knowledge base names regexps.
    remoteApiServer.accessService.addRolesToKb({ "ROLE_ADMIN2":[".*"], "ROLE_ANONYMOUS2":["boot", "python"]})

    # Simple access configuration: role -> event names regexps.
    remoteApiServer.accessService.addRolesToSendEvent({ "ROLE_ADMIN2":[".*"], "ROLE_ANONYMOUS2":[]})
    remoteApiServer.accessService.addRolesToSubscribeEvent({ "ROLE_ADMIN2":[".*"], "ROLE_ANONYMOUS2":[".*"]})

def onStartup():
    # Configure the access service on startup.
    configureAccessService()

def onAfterReload():
    # Reconfigure the access service after each reload.
    configureAccessService()