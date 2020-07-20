"""
Sponge Knowledge Base
Using plugins
"""

def onInit():
    # Variables for assertions only
    sponge.setVariable("connectionName", None)
    sponge.setVariable("echoConfig", None)

class PluginTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("e1")
    def onRun(self, event):
        self.logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
        sponge.setVariable("connectionName", connectionPlugin.connectionName)

def onStartup():
    sponge.logger.debug("Connection name: {}", connectionPlugin.connectionName)
    sponge.event("e1").send()

    sponge.logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
    sponge.setVariable("echoConfig", echoPlugin.echoConfig)
    for i in range(echoPlugin.count):
        sponge.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
