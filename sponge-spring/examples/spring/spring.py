"""
Sponge Knowledge Base
Spring integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    sponge.setVariable("springBeanValue", None)

class SpringTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("spring")
    def onRun(self, event):
        beanValue = spring.context.getBean("testBean")
        self.logger.debug("Bean value = {}", beanValue)
        sponge.setVariable("springBeanValue", beanValue)

def onStartup():
    sponge.event("spring").send()

