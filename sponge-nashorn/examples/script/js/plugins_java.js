/**
 * Sponge Knowledge base
 * Using plugins
 */

function onInit() {
    // Variables for assertions only
    EPS.setVariable("connectionName", null);
    EPS.setVariable("echoConfig", null);
}

var PluginTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.event = "e1";
    },
    run: function(self, event) {
        self.logger.debug("Connection name is still: {}", connectionPlugin.connectionName);
        EPS.setVariable("connectionName", connectionPlugin.connectionName);
    }
});

function onStartup() {
    EPS.logger.debug("Connection name: {}", connectionPlugin.connectionName);
    EPS.event("e1").send();

    EPS.logger.info("Echo plugin config: {}", echoPlugin.echoConfig);
    EPS.setVariable("echoConfig", echoPlugin.echoConfig);
    for (i = 0; i < echoPlugin.count; i++) { 
        EPS.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo);
    }
}
