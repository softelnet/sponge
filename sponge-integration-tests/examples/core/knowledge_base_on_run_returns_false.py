"""
Sponge Knowledge base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    sponge.setVariable("onRun", AtomicBoolean(False))

def onRun():
    sponge.logger.info("In run once mode. Executing a script and stopping the engine")
    sponge.getVariable("onRun").set(True)
    return False # Run once mode
