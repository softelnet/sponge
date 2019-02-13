/**
 * Sponge Knowledge base
 * Using plugins
 */

void onInit() {
    // Variables for assertions only
    sponge.setVariable("connectionName", null)
    sponge.setVariable("echoConfig", null)
}

class PluginTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("e1")
    }
    void onRun(Event event) {
        def connectionPlugin = sponge.getPlugin("connectionPlugin")
        this.logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
        sponge.setVariable("connectionName", connectionPlugin.connectionName)
    }
}

void onStartup() {
    sponge.logger.debug("Connection name: {}", connectionPlugin.connectionName)
    sponge.event("e1").send()

    sponge.logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
    sponge.setVariable("echoConfig", echoPlugin.echoConfig)
    for (i in (0..<echoPlugin.count)) {
        sponge.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
    }
}

