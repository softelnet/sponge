/**
 * Sponge Knowledge base
 * Script - Overriding
 * Note that auto-enable is turned off in the configuration.
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    EPS.setVariable("receivedEventA1", new AtomicInteger(0))
    EPS.setVariable("receivedEventA2", new AtomicInteger(0))
    EPS.setVariable("functionA1", new AtomicInteger(0))
    EPS.setVariable("functionA2", new AtomicInteger(0))
}

// Unfortunately in Groovy you cannot define a class or a function twice. If you want to reload an event processor or an action, you have
// to put it in a separate file and use EPS.reloadClass() method. That separate file could be modified and reloaded.

// Execute immediately while loading
EPS.reloadClass(TriggerA)
EPS.enable(TriggerA)

// Execute immediately while loading
EPS.reloadClass(TriggerA)
EPS.enable(TriggerA)

void onStartup() {
    EPS.event("a").send()
    functionA()
}

void functionA() {
    EPS.reloadClass(FunctionAAction)
    EPS.enable(FunctionAAction)
    EPS.callAction("FunctionAAction")
}

