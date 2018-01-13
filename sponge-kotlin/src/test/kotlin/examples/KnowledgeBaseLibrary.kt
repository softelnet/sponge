/*
 * Sponge Knowledge base
 * Library use
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections

class KnowledgeBaseLibrary : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("hostStatus", Collections.synchronizedMap(HashMap<String, String>()))
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)

        fun checkPageStatus(host: String): String {
            try {
                logger.info("Trying {}...", host);
                val connection = URL("https://" + host).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                logger.info("Host {} status: {}", host, connection.responseCode);
                return connection.responseCode.toString()
            } catch(e: Exception) {
                logger.debug("Host {} error: {}", host, e)
                return "ERROR"
            }
        }
    }

    class HttpStatusTrigger : KTrigger() {
        override fun onConfigure() = setEvent("checkStatus")

        override fun onRun(event: Event) {
            val status = checkPageStatus(event.get<String>("host"))
            eps.getVariable<MutableMap<String, String>>("hostStatus").put(event.get<String>("host"), status)
        }
    }

    override fun onStartup() {
        eps.event("checkStatus").set("host", "www.wikipedia.org.unknown").send()
        eps.event("checkStatus").set("host", "www.wikipedia.org").send()
    }
}
