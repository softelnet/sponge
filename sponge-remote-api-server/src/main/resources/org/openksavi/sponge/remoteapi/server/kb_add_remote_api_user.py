"""
Sponge Knowledge Base
Add a Remote API user
"""

def onLoad():
    sponge.enableJavaByScan("org.openksavi.sponge.remoteapi.server.kb")

def onRun():
    if sponge.engine.args.size() < 1:
        raise Exception("Missing a password file argument")

    sponge.call("AddRemoteApiUser", [sponge.engine.args[0]])

    return False

