"""
Sponge Knowledge base
"""

from java.util.concurrent.atomic import AtomicBoolean
import re, collections

# Reject news with empty or short titles.
class NewsFilter(Filter):
    def configure(self):
        self.eventName = "news"
    def accepts(self, event):
        title = event.get("title")
        words = len(re.findall("\w+", title))
        return title is not None and words >= int(EPS.getVariable("newsFilterWordThreshold"))

# Log every news.
class LogNewsTrigger(Trigger):
    def configure(self):
        self.eventName = "news"
    def run(self, event):
        print("News from {} - {}".format(event.get("source"), event.get("title")))

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
        self.eventName = "alarm"
    def run(self, event):
        print("Sound the alarm! {}".format(event.get("message")))
        print("Last news was:\n{}".format(EPS.callAction("EmphasizeAction", storagePlugin.storedValue[-1])))
        EPS.getVariable("alarmSounded").set(True)

# Start only one instance of this aggregator for the system. Note that in this example data is stored in a plugin,
# not in this aggregator.
class LatestNewsAggregator(Aggregator):
    instanceStarted = AtomicBoolean(False)
    def configure(self):
        self.eventNames = ["news"]
    def init(self):
        storagePlugin.storedValue = collections.deque(maxlen=int(EPS.getVariable("latestNewsMaxSize", 2)))
    def acceptsAsFirst(self, event):
        return LatestNewsAggregator.instanceStarted.compareAndSet(False, True)
    def onEvent(self, event):
        storagePlugin.storedValue.append(event.get("title"))
        self.logger.debug("{} - latest news: {}", self.hashCode(), str(storagePlugin.storedValue))

