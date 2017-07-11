# Sponge Knowledge base
# Standard Ruby library use

require 'net/http'
require 'openssl'

java_import java.util.Collections
java_import java.util.HashMap

def onInit
    # Variables for assertions only
    $EPS.setVariable("hostStatus", Collections.synchronizedMap(HashMap.new))
end

def checkPageStatus(host)
    begin
        $EPS.logger.debug("Trying {}...", host)
        uri = URI("https://" + host)
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        response = http.request(Net::HTTP::Get.new(uri.request_uri))
        $EPS.logger.debug("Host {} status: {}", host, response.code)
        return response.code
    rescue SocketError => e
        $EPS.logger.debug("Host {} error: {}", host, e)
        return "ERROR"
    end
end

class HttpStatusTrigger < Trigger
    def configure
        self.event = "checkStatus"
    end
    def init
        @mutex = Mutex.new
    end
    def run(event)
        # Using synchronize block in this example because we are not sure that Net::HTTP is thread safe.
        @mutex.synchronize {
            status = checkPageStatus(event.get("host"))
            $EPS.getVariable("hostStatus").put(event.get("host"), status)
        }
    end
end

def onStartup
    $EPS.event("checkStatus").set("host", "www.wikipedia.org.unknown").send()
    $EPS.event("checkStatus").set("host", "www.wikipedia.org").send()
end

