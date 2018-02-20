"""
Sponge Knowledge base
Test - onRun
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    EPS.setVariable("onRun", AtomicBoolean(False))

def onRun():
    EPS.getVariable("onRun").set(True)
    return 0 # Incorrect result type
