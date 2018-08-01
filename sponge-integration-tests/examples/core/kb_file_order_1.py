"""
Sponge Knowledge base
Knowledge base file order
"""

from java.util import ArrayList

def onInit():
    # Variables for assertions only
    sponge.setVariable("order", ArrayList())

def onStartup():
    sponge.getVariable("order").add(1)
