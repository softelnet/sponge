/*
 * Sponge Knowledge base
 * Using plugins
 */

import org.openksavi.sponge.examples.ConnectionPlugin
import org.openksavi.sponge.examples.EchoPlugin

fun onInit() {
    // Variables for assertions only
    sponge.setVariable("connectionName", null)
    sponge.setVariable("echoConfig", null)
}

class PluginTrigger : Trigger() {
    override fun onConfigure() {
        withEvent("e1")
    }
    override fun onRun(event: Event) {
        val connectionPlugin = sponge.getPlugin(ConnectionPlugin::class.java)
        logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
        sponge.setVariable("connectionName", connectionPlugin.connectionName)
    }
}

fun onStartup() {
    val connectionPlugin = sponge.getPlugin(ConnectionPlugin::class.java)
    sponge.logger.debug("Connection name: {}", connectionPlugin.connectionName)
    sponge.event("e1").send()

    val echoPlugin = sponge.getPlugin(EchoPlugin::class.java)
    sponge.logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
    sponge.setVariable("echoConfig", echoPlugin.echoConfig)
    for (i in 1 until echoPlugin.count) {
        sponge.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
    }
}

