/*
 * Sponge Knowledge base
 * Defining plugins in a Kotlin knowledge base.
 */

import org.openksavi.sponge.examples.ConnectionPlugin
import org.openksavi.sponge.examples.EchoPlugin

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("valueBefore", null)
    EPS.setVariable("valueAfter", null)
}

/** Example plugin defined in the Kotlin knowledge base. */
class KotlinKbPlugin : Plugin() {
    var storedValue: Any? = null

    override fun onConfigure(configuration: Configuration) {
        storedValue = configuration.getString("storedValue", "default")
    }

    override fun onInit() = logger.debug("Initializing {}", name)

    override fun onStartup() = logger.debug("Starting up {}", name)
}

class PluginTrigger : Trigger() {
    override fun onConfigure() = setEvent("e1")
    override fun onRun(event: Event) {
        val kotlinKbPlugin = eps.getPlugin(KotlinKbPlugin::class.java)
        val valueBefore = kotlinKbPlugin.storedValue
        logger.info("Plugin stored value: {}", valueBefore)
        eps.setVariable("valueBefore", valueBefore)
        kotlinKbPlugin.storedValue = event.get<Any>("value")
        val valueAfter = kotlinKbPlugin.storedValue
        logger.info("New stored value: {}", valueAfter)
        eps.setVariable("valueAfter", valueAfter)
    }
}

fun onStartup() {
    EPS.event("e1").set("value", "Value B").send()
}
