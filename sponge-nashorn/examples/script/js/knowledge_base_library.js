/**
 * Sponge Knowledge base
 * Standard Java library use
 */

function onInit() {
    // Variables for assertions only
    sponge.setVariable("hostStatus", java.util.Collections.synchronizedMap(new java.util.HashMap()));
}

function checkPageStatus(host) {
    try {
        sponge.logger.debug("Trying {}...", host);
        var connection = new java.net.URL("https://" + host).openConnection();
        connection.requestMethod = "GET";
        connection.connect();
        sponge.logger.debug("Host {} status: {}", host, connection.responseCode);
        return connection.responseCode.toString();
    } catch (e) {
        sponge.logger.debug("Host {} error: {}", host, e.message);
        return "ERROR";
    }
}

var HttpStatusTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.event = "checkStatus";
    },
    onRun: function(self, event) {
        var status = checkPageStatus(event.get("host"));
        sponge.getVariable("hostStatus").put(event.get("host"), status);
    }
});

function onStartup() {
    sponge.event("checkStatus").set("host", "www.wikipedia.org.unknown").send();
    sponge.event("checkStatus").set("host", "www.wikipedia.org").send();
}
