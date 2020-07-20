/**
 * Sponge Knowledge Base
 * Triggers - Incorrect event pattern
 */

class TriggerAPattern extends Trigger {
    void onConfigure() {
        this.withEvent("a.**")
    }
    void onRun(Event event) {
    }
}

// It seems that a Groovy-based knowledge base must have at east one function (may be empty). Otherwise you may get
// org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack exception.
void onStartup() {
}
