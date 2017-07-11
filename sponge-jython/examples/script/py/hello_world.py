"""
Sponge Knowledge base
Hello world
"""

class HelloWorldTrigger(Trigger):
    def configure(self):
        self.event = "helloEvent"
    def run(self, event):
        print event.get("say")

def onStartup():
    EPS.event("helloEvent").set("say", "Hello World!").send()
