"""
Sponge Knowledge base
Spring integration
"""

from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    # Variables for assertions only
    EPS.setVariable("springBeanValue", None)

class SpringTrigger(Trigger):
    def configure(self):
        self.eventName = "spring"
    def run(self, event):
        beanValue = spring.context.getBean("testBean")
        self.logger.debug("Bean value = {}", beanValue)
        EPS.setVariable("springBeanValue", beanValue)

def onStartup():
    EPS.event("spring").send()

