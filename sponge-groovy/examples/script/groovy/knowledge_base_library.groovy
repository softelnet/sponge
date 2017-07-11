/**
 * Sponge Knowledge base
 * Standard Python library use
 */

import org.springframework.http.*
import org.springframework.web.client.*

void onInit() {
    // Variables for assertions only
    EPS.setVariable("hostStatus", Collections.synchronizedMap(new HashMap()))
}

class HttpStatusTrigger extends Trigger {
    void configure() {
        this.event = "checkStatus"
    }
    void run(Event event) {
        String status = checkPageStatus(event.get("host"))
        EPS.getVariable("hostStatus").put(event.get("host"), status)
    }
    String checkPageStatus(String host) {
        try {
            this.logger.debug("Trying {}...", host)
            RestTemplate template = new RestTemplate()
            ResponseEntity<String> entity = template.getForEntity("https://" + host, String.class)
            def status = entity.getStatusCode()

            this.logger.debug("Host {} status: {}", host, status.value)
            return (status.value as String)
        } catch(Exception e) {
            this.logger.debug("Host {} error: {}", host, e.message)
            return "ERROR"
        }
    }
}

void onStartup() {
    EPS.event("checkStatus").set("host", "www.wikipedia.org.unknown").send()
    EPS.event("checkStatus").set("host", "www.wikipedia.org").send()
}