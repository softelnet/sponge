# Sponge Knowledge Base
# Using plugins

def onInit
    # Variables for assertions only
    $sponge.setVariable("connectionName", nil)
    $sponge.setVariable("echoConfig", nil)
end

class PluginTrigger < Trigger
    def onConfigure
        self.withEvent("e1")
    end
    def onRun(event)
        self.logger.debug("Connection name is still: {}", $connectionPlugin.connectionName)
        $sponge.setVariable("connectionName", $connectionPlugin.connectionName)
    end
end

def onStartup
    $sponge.logger.debug("Connection name: {}", $connectionPlugin.connectionName)
    $sponge.event("e1").send()

    $sponge.logger.info("Echo plugin config: {}", $echoPlugin.echoConfig)
    $sponge.setVariable("echoConfig", $echoPlugin.echoConfig)
    for i in (0...$echoPlugin.count)
        $sponge.logger.info("\tEcho from echo plugin: {}", $echoPlugin.echo)
    end
end

