/**
 * Sponge Knowledge base
 * Using plugins
 */

void onInit() {
    // Variables for assertions only
    EPS.setVariable("connectionName", null)
    EPS.setVariable("echoConfig", null)
}

class PluginTrigger extends Trigger {
    void onConfigure() {
        this.event = "e1"
    }
    void onRun(Event event) {
        def connectionPlugin = EPS.getPlugin("connectionPlugin")
        this.logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
        EPS.setVariable("connectionName", connectionPlugin.connectionName)
    }
}

void onStartup() {
    EPS.logger.debug("Connection name: {}", connectionPlugin.connectionName)
    EPS.event("e1").send()

    EPS.logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
    EPS.setVariable("echoConfig", echoPlugin.echoConfig)
    for (i in (0..<echoPlugin.count)) {
        EPS.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
    }
}

