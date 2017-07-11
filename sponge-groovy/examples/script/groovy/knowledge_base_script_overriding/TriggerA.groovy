import org.openksavi.sponge.event.Event
import org.openksavi.sponge.groovy.GroovyTrigger as Trigger

class TriggerA extends Trigger {
    void configure() {
        this.event = "a"
    }
    void run(Event event) {
        EPS.getVariable("receivedEventA2").set(2)
    }
}