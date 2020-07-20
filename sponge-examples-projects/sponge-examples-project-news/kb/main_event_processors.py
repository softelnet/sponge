"""
Sponge Knowledge Base
"""

from java.util.concurrent.atomic import AtomicBoolean
import re, collections

# Reject news with empty or short titles.
class NewsFilter(Filter):
    def onConfigure(self):
        self.withEvent("news")
    def onAccept(self, event):
        title = event.get("title")
        words = len(re.findall("\w+", title))
        return title is not None and words >= int(sponge.getVariable("newsFilterWordThreshold"))

# Log every news.
class LogNewsTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("news")
    def onRun(self, event):
        self.logger.info("News from " + event.get("source") + " - " + event.get("title"))

# Send 'alarm' event when news stop arriving for 3 seconds.
class NoNewNewsAlarmRule(Rule):
    def onConfigure(self):
        self.withEvents(["news n1", "news n2 :none"]).withDuration(Duration.ofSeconds(3))
    def onRun(self, event):
        sponge.event("alarm").set("severity", 1).set("message", "No new news for " + str(self.meta.duration.seconds) + "s.").send()

# Handles 'alarm' events.
class AlarmTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("alarm")
    def onRun(self, event):
        self.logger.info("Sound the alarm! {}", event.get("message"))
        self.logger.info("Last news was (repeat {} time(s)):\n{}", echoPlugin.count,
                         sponge.call("EmphasizeAction", [echoPlugin.echo(storagePlugin.storedValue[-1])]))
        sponge.getVariable("alarmSounded").set(True)

# Start only one instance of this correlator for the system. Note that in this example data is stored in a plugin,
# not in this correlator.
class LatestNewsCorrelator(Correlator):
    def onConfigure(self):
        self.withEvent("news").withMaxInstances(1)
    def onInit(self):
        storagePlugin.storedValue = collections.deque(maxlen=int(sponge.getVariable("latestNewsMaxSize", 2)))
    def onEvent(self, event):
        storagePlugin.storedValue.append(event.get("title"))
        self.logger.debug("{} - latest news: {}", self.hashCode(), str(storagePlugin.storedValue))

