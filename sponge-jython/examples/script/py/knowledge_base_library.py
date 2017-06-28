"""
Sponge Knowledge base
Standard Python library use
"""

from httplib import HTTPSConnection
from java.util import Collections, HashMap

def onInit():
    # Variables for assertions only
    EPS.setVariable("hostStatus", Collections.synchronizedMap(HashMap()))

def checkPageStatus(host):
    try:
        EPS.logger.debug("Trying {}...", host)
        connection = HTTPSConnection(host)
        connection.request("GET", "/")
        response = connection.getresponse()
        EPS.logger.debug("Host {} status: {}", host, response.status)
        return str(response.status)
    except Exception, e:
        EPS.logger.debug("Host {} error: {}", host, e)
        return "ERROR"

class HttpStatusTrigger(Trigger):
    def configure(self):
        self.eventName = "checkStatus"
    def run(self, event):
        status = checkPageStatus(event.get("host"))
        EPS.getVariable("hostStatus").put(event.get("host"), status)

def onStartup():
    EPS.event("checkStatus").set("host", "www.wikipedia.org.unknown").send()
    EPS.event("checkStatus").set("host", "www.wikipedia.org").send()








