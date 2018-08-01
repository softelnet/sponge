# Sponge Knowledge base
# Standard Ruby library use

require 'net/http'
require 'openssl'

java_import java.util.Collections
java_import java.util.HashMap

def onInit
    # Variables for assertions only
    $sponge.setVariable("hostStatus", Collections.synchronizedMap(HashMap.new))
end

def checkPageStatus(host)
    begin
        $sponge.logger.debug("Trying {}...", host)
        uri = URI("https://" + host)
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        response = http.request(Net::HTTP::Get.new(uri.request_uri))
        $sponge.logger.debug("Host {} status: {}", host, response.code)
        return response.code
    rescue SocketError => e
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
        # Using synchronize block in this example because we are not sure that Net::HTTP is thread safe.
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

