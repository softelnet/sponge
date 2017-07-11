"""
Sponge Knowledge base
"""

from java.util.concurrent.atomic import AtomicBoolean
import re, collections

# Reject news with empty or short titles.
class NewsFilter(Filter):
    def configure(self):
        self.event = "news"
    def accepts(self, event):
        title = event.get("title")
        words = len(re.findall("\w+", title))
        return title is not None and words >= int(EPS.getVariable("newsFilterWordThreshold"))

# Log every news.
class LogNewsTrigger(Trigger):
    def configure(self):
        self.event = "news"
    def run(self, event):
        self.logger.info("News from " + event.get("source") + " - " + event.get("title"))

# Send 'alarm' event when news stop arriving for 3 seconds.
class NoNewNewsAlarmRule(Rule):
    def configure(self):
        self.events = ["news n1", "news n2 :none"]
        self.duration = Duration.ofSeconds(3)
    def run(self, event):
        EPS.event("alarm").set("severity", 1).set("message", "No new news for " + str(self.duration.seconds) + "s.").send()

# Handles 'alarm' events.
class AlarmTrigger(Trigger):
    def configure(self):
        self.event = "alarm"
    def run(self, event):
        self.logger.info("Sound the alarm! {}", event.get("message"))
        self.logger.info("Last news was (repeat {} time(s)):\n{}", echoPlugin.count,
                         EPS.callAction("EmphasizeAction", echoPlugin.echo(storagePlugin.storedValue[-1])))
        EPS.getVariable("alarmSounded").set(True)

# Start only one instance of this correlator for the system. Note that in this example data is stored in a plugin,
# not in this correlator.
class LatestNewsCorrelator(Correlator):
    def configure(self):
        self.events = ["news"]
        self.maxInstances = 1
    def init(self):
        storagePlugin.storedValue = collections.deque(maxlen=int(EPS.getVariable("latestNewsMaxSize", 2)))
    def onEvent(self, event):
        storagePlugin.storedValue.append(event.get("title"))
        self.logger.debug("{} - latest news: {}", self.hashCode(), str(storagePlugin.storedValue))

