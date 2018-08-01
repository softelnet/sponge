/**
 * Sponge Knowledge base
 * Script - Overriding
 * Note that auto-enable is turned off in the configuration.
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    sponge.setVariable("receivedEventA1", new AtomicInteger(0))
    sponge.setVariable("receivedEventA2", new AtomicInteger(0))
    sponge.setVariable("functionA1", new AtomicInteger(0))
    sponge.setVariable("functionA2", new AtomicInteger(0))
}

// Unfortunately in Groovy you cannot define a class or a function twice. If you want to reload an event processor or an action, you have
// to put it in a separate file and use sponge.reloadClass() method. That separate file could be modified and reloaded.

// Execute immediately while loading
sponge.reloadClass(TriggerA)
sponge.enable(TriggerA)

// Execute immediately while loading
sponge.reloadClass(TriggerA)
sponge.enable(TriggerA)

void onStartup() {
    sponge.event("a").send()
    functionA()
}

void functionA() {
    sponge.reloadClass(FunctionAAction)
    sponge.enable(FunctionAAction)
    sponge.call("FunctionAAction")
}

