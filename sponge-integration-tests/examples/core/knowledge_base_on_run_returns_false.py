"""
Sponge Knowledge base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    EPS.setVariable("onRun", AtomicBoolean(False))

def onRun():
    EPS.logger.info("In run once mode. Executing a script and stopping the engine")
    EPS.getVariable("onRun").set(True)
    return False # Run once mode
