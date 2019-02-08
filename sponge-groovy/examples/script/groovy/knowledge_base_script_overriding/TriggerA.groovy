import org.openksavi.sponge.event.Event
import org.openksavi.sponge.groovy.GroovyTrigger as Trigger

class TriggerA extends Trigger {
    void onConfigure() {
        this.withEvent("a")
    }
    void onRun(Event event) {
        sponge.getVariable("receivedEventA2").set(2)
    }
}