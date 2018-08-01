import org.openksavi.sponge.event.Event
import org.openksavi.sponge.groovy.GroovyAction as Action

class FunctionAAction extends Action {
    Object onCall(Object[] args) {
        sponge.getVariable("functionA2").set(2)
        return null
    }
}