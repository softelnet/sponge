/**
 * Sponge Knowledge base
 * Standard Java library use
 */

function onInit() {
    // Variables for assertions only
    EPS.setVariable("hostStatus", java.util.Collections.synchronizedMap(new java.util.HashMap()));
}

function checkPageStatus(host) {
    try {
        EPS.logger.debug("Trying {}...", host);
        var connection = new java.net.URL("https://" + host).openConnection();
        connection.requestMethod = "GET";
        connection.connect();
        EPS.logger.debug("Host {} status: {}", host, connection.responseCode);
        return connection.responseCode.toString();
    } catch (e) {
        EPS.logger.debug("Host {} error: {}", host, e.message);
        return "ERROR";
    }
}

var HttpStatusTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "checkStatus";
    },
    run: function(self, event) {
        var status = checkPageStatus(event.get("host"));
        EPS.getVariable("hostStatus").put(event.get("host"), status);
    }
});

function onStartup() {
    EPS.event("checkStatus").set("host", "www.wikipedia.org.unknown").send();
    EPS.event("checkStatus").set("host", "www.wikipedia.org").send();
}
