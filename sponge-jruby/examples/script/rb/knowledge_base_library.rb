# Sponge Knowledge base
# Library use

java_import java.util.Collections
java_import java.util.HashMap
java_import java.net.HttpURLConnection
java_import java.net.URL
java_import java.util.Collections

def onInit
    # Variables for assertions only
    $sponge.setVariable("hostStatus", Collections.synchronizedMap(HashMap.new))
end

def checkPageStatus(host)
    begin
        $sponge.logger.debug("Trying {}...", host)
        connection = URL.new("https://" + host).openConnection()
        connection.requestMethod = "GET"
        connection.connect()
        $sponge.logger.info("Host {} status: {}", host, connection.responseCode);
        return connection.responseCode.to_s
    rescue Exception => e
        $sponge.logger.debug("Host {} error: {}", host, e)
        return "ERROR"
    end
end

class HttpStatusTrigger < Trigger
    def onConfigure
        self.event = "checkStatus"
    end
    def onInit
        @mutex = Mutex.new
    end
    def onRun(event)
        # Using synchronize block.
        @mutex.synchronize {
            status = checkPageStatus(event.get("host"))
            $sponge.getVariable("hostStatus").put(event.get("host"), status)
        }
    end
end

def onStartup
    $sponge.event("checkStatus").set("host", "www.wikipedia.org.unknown").send()
    $sponge.event("checkStatus").set("host", "www.wikipedia.org").send()
end

