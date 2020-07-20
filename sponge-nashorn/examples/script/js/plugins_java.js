/**
 * Sponge Knowledge Base
 * Using plugins
 */

function onInit() {
    // Variables for assertions only
    sponge.setVariable("connectionName", null);
    sponge.setVariable("echoConfig", null);
}

var PluginTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("e1");
    },
    onRun: function(self, event) {
        self.logger.debug("Connection name is still: {}", connectionPlugin.connectionName);
        sponge.setVariable("connectionName", connectionPlugin.connectionName);
    }
});

function onStartup() {
    sponge.logger.debug("Connection name: {}", connectionPlugin.connectionName);
    sponge.event("e1").send();

    sponge.logger.info("Echo plugin config: {}", echoPlugin.echoConfig);
    sponge.setVariable("echoConfig", echoPlugin.echoConfig);
    for (i = 0; i < echoPlugin.count; i++) { 
        sponge.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo);
    }
}
