class TriggerA extends Trigger {
    void onConfigure() {
        this.withEvent("a")
    }
    void onRun(Event event) {
        sponge.getVariable("receivedEventA2").set(2)
    }
}