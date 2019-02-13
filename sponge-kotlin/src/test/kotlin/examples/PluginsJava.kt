/*
 * Sponge Knowledge base
 * Using plugins
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.ConnectionPlugin
import org.openksavi.sponge.examples.EchoPlugin
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger

class PluginsJava : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("connectionName", null)
        sponge.setVariable("echoConfig", null)
    }

    class PluginTrigger : KTrigger() {
        override fun onConfigure() {
            withEvent("e1")
        }
        override fun onRun(event: Event) {
            val connectionPlugin = sponge.getPlugin(ConnectionPlugin::class.java)
            logger.debug("Connection name is still: {}", connectionPlugin.connectionName)
            sponge.setVariable("connectionName", connectionPlugin.connectionName)
        }
    }

    override fun onStartup() {
        val connectionPlugin = sponge.getPlugin(ConnectionPlugin::class.java)
        logger.debug("Connection name: {}", connectionPlugin.connectionName)
        sponge.event("e1").send()

        val echoPlugin = sponge.getPlugin(EchoPlugin::class.java)
        logger.info("Echo plugin config: {}", echoPlugin.echoConfig)
        sponge.setVariable("echoConfig", echoPlugin.echoConfig)
        for (i in 1 until echoPlugin.count) {
            logger.info("\tEcho from echo plugin: {}", echoPlugin.echo)
        }
    }
}

