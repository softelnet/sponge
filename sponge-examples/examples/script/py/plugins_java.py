"""
Sponge Knowledge base
Using plugins
"""

def onInit():
    # Variables for assertions only
    EPS.setVariable("connectionName", None)
    EPS.setVariable("echoConfig", None)

class PluginTrigger(Trigger):
    def configure(self):
        self.eventName = "e1"
    def run(self, event):
        self.logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
        EPS.setVariable("connectionName", connectionPlugin.connectionName)

def onStartup():
    EPS.logger.debug("Connection name: {}", connectionPlugin.connectionName)
    EPS.event("e1").send()

    EPS.logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
    EPS.setVariable("echoConfig", echoPlugin.echoConfig)
    for i in range(echoPlugin.count):
        EPS.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
