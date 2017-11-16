"""
Sponge Knowledge base
"""

from java.util.concurrent.atomic import AtomicInteger, AtomicBoolean

# Set initial values for variables.
def onInit():
    EPS.setVariable("alarmSounded", AtomicBoolean(False))
    EPS.setVariable("alarmForwarded", AtomicBoolean(False))
    EPS.setVariable("sourceRoutesStopped", AtomicBoolean(False))
