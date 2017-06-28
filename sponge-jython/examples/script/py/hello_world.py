"""
Sponge Knowledge base
Hello world
"""

class HelloWorldTrigger(Trigger):
    def configure(self):
        self.eventName = "helloEvent"
    def run(self, event):
        print event.get("say")

def onStartup():
    EPS.event("helloEvent").set("say", "Hello World!").send()
