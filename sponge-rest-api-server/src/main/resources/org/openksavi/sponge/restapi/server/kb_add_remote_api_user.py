"""
Sponge Knowledge base
Add a REST API user
"""

def onLoad():
    sponge.enableJavaByScan("org.openksavi.sponge.restapi.server.kb")

def onRun():
    if sponge.engine.args.size() < 1:
        raise Exception("Missing a password file argument")

    sponge.call("AddRemoteApiUser", [sponge.engine.args[0]])

    return False

