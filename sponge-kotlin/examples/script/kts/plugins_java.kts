/*
 * Sponge Knowledge base
 * Using plugins
 */

import org.openksavi.sponge.examples.ConnectionPlugin
import org.openksavi.sponge.examples.EchoPlugin

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("connectionName", null)
    EPS.setVariable("echoConfig", null)
}

class PluginTrigger : Trigger() {
    override fun onConfigure() = setEvent("e1")
    override fun onRun(event: Event) {
        val connectionPlugin = eps.getPlugin(ConnectionPlugin::class.java)
        logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
        eps.setVariable("connectionName", connectionPlugin.connectionName)
    }
}

fun onStartup() {
    val connectionPlugin = EPS.getPlugin(ConnectionPlugin::class.java)
    EPS.logger.debug("Connection name: {}", connectionPlugin.connectionName)
    EPS.event("e1").send()

    val echoPlugin = EPS.getPlugin(EchoPlugin::class.java)
    EPS.logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
    EPS.setVariable("echoConfig", echoPlugin.echoConfig)
    for (i in 1 until echoPlugin.count) {
        EPS.logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
    }
}

