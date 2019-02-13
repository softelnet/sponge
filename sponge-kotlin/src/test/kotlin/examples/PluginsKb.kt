/*
 * Sponge Knowledge base
 * Defining plugins in a Kotlin knowledge base.
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.config.Configuration
import org.openksavi.sponge.event.Event
import org.openksavi.sponge.examples.ConnectionPlugin
import org.openksavi.sponge.examples.EchoPlugin
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KPlugin
import org.openksavi.sponge.kotlin.KTrigger

class PluginsKb : KKnowledgeBase() {

    override fun onInit() {
        // Variables for assertions only
        sponge.setVariable("valueBefore", null)
        sponge.setVariable("valueAfter", null)
    }

    /** Example plugin defined in the Kotlin knowledge base. */
    class KotlinKbPlugin : KPlugin() {
        var storedValue: Any? = null

        override fun onConfigure(configuration: Configuration) {
            storedValue = configuration.getString("storedValue", "default")
        }

        override fun onInit() = logger.debug("Initializing {}", name)

        override fun onStartup() = logger.debug("Starting up {}", name)
    }

    class PluginTrigger : KTrigger() {
        override fun onConfigure() {
            withEvent("e1")
        }
        override fun onRun(event: Event) {
            val kotlinKbPlugin = sponge.getPlugin(KotlinKbPlugin::class.java)
            val valueBefore = kotlinKbPlugin.storedValue
            logger.info("Plugin stored value: {}", valueBefore)
            sponge.setVariable("valueBefore", valueBefore)
            kotlinKbPlugin.storedValue = event.get<Any>("value")
            val valueAfter = kotlinKbPlugin.storedValue
            logger.info("New stored value: {}", valueAfter)
            sponge.setVariable("valueAfter", valueAfter)
        }
    }

    override fun onStartup() {
        sponge.event("e1").set("value", "Value B").send()
    }
}
