"""
Sponge Knowledge base
Standard Python library use
"""

from httplib import HTTPSConnection
from java.util import Collections, HashMap

def onInit():
    # Variables for assertions only
    sponge.setVariable("hostStatus", Collections.synchronizedMap(HashMap()))

def checkPageStatus(host):
    try:
        sponge.logger.debug("Trying {}...", host)
        connection = HTTPSConnection(host)
        connection.request("GET", "/")
        response = connection.getresponse()
        sponge.logger.debug("Host {} status: {}", host, response.status)
        return str(response.status)
    except Exception, e:
        sponge.logger.debug("Host {} error: {}", host, e)
        return "ERROR"

class HttpStatusTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("checkStatus")
    def onRun(self, event):
        status = checkPageStatus(event.get("host"))
        sponge.getVariable("hostStatus").put(event.get("host"), status)

def onStartup():
    sponge.event("checkStatus").set("host", "www.wikipedia.org.unknown").send()
    sponge.event("checkStatus").set("host", "www.wikipedia.org").send()








