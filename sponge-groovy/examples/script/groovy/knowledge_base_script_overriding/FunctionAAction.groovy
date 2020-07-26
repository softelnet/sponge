class FunctionAAction extends Action {
    Object onCall(Object[] args) {
        sponge.getVariable("functionA2").set(2)
        return null
    }
}