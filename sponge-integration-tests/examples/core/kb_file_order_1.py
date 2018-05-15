"""
Sponge Knowledge base
Knowledge base file order
"""

from java.util import ArrayList

def onInit():
    # Variables for assertions only
    EPS.setVariable("order", ArrayList())

def onStartup():
    EPS.getVariable("order").add(1)
