"""
Sponge Knowledge Base
"""

# Trigger that executes a function passed as an event attribute.
class ExecuteTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("execute")
    def onRun(self, event):
        event.get("function")()

def onStartup():
    # Send any news event for a test.
    sponge.event("news").set("source", "TEST").set("title", "This is news not from RSS").sendAfter(1000)

    # Simulate lack of new news after the configured time, by stopping Camel routes that read RSS sources (implemented in
    # a service class provided as a Spring bean).
    sponge.event("execute").set("function",
        lambda: spring.context.getBean("camelService").stopSourceRoutes()).sendAfter(int(sponge.getVariable("durationOfReadingRss", 2)) * 1000)
