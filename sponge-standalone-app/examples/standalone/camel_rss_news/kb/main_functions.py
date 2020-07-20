"""
Sponge Knowledge Base
"""

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

# Set initial values for variables.
def onInit():
    sponge.setVariable("alarmSounded", AtomicBoolean(False))
