import org.openksavi.sponge.event.Event
import org.openksavi.sponge.groovy.GroovyAction as Action

class FunctionAAction extends Action {
    Object run(Object[] args) {
        EPS.getVariable("functionA2").set(2)
        return null
    }
}