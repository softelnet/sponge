"""
Sponge Knowledge base
"""

from java.util.concurrent.atomic import AtomicBoolean

# Set initial values for variables.
def onInit():
    EPS.setVariable("alarmSounded", AtomicBoolean(False))
