"""
Sponge Knowledge base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    sponge.setVariable("onRun", AtomicBoolean(False))

def onRun():
    sponge.getVariable("onRun").set(True)
    return 0 # Incorrect result type
