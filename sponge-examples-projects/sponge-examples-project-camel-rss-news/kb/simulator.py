"""
Sponge Knowledge base
"""

# Trigger that executes a function passed as an event attribute.
class ExecuteTrigger(Trigger):
    def configure(self):
        self.eventName = "execute"
    def run(self, event):
        event.get("function")()

def onStartup():
    # Simulate lack of new news after the configured time, by stopping Camel routes that read RSS sources (implemented in
    # a service class provided as a Spring bean).
    EPS.event("execute").set("function",
        lambda: spring.context.getBean("camelService").stopSourceRoutes()).sendAfter(int(EPS.getVariable("durationOfReadingRss", 2)) * 1000)
