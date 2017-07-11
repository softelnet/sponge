# Sponge Knowledge base
# Using plugins

def onInit
    # Variables for assertions only
    $EPS.setVariable("connectionName", nil)
    $EPS.setVariable("echoConfig", nil)
end

class PluginTrigger < Trigger
    def configure
        self.event = "e1"
    end
    def run(event)
        self.logger.debug("Connection name is still: {}", $connectionPlugin.connectionName)
        $EPS.setVariable("connectionName", $connectionPlugin.connectionName)
    end
end

def onStartup
    $EPS.logger.debug("Connection name: {}", $connectionPlugin.connectionName)
    $EPS.event("e1").send()

    $EPS.logger.info("Echo plugin config: {}", $echoPlugin.echoConfig)
    $EPS.setVariable("echoConfig", $echoPlugin.echoConfig)
    for i in (0...$echoPlugin.count)
        $EPS.logger.info("\tEcho from echo plugin: {}", $echoPlugin.echo)
    end
end

