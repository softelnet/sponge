"""
Sponge Knowledge base
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
        print("News from {} - {}".format(event.get("source"), event.get("title").encode('utf-8')))

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
        print("Sound the alarm! {}".format(event.get("message").encode('utf-8')))
        print("Last news was:\n{}".format(sponge.call("EmphasizeAction", [storagePlugin.storedValue[-1]]).encode('utf-8')))
        sponge.getVariable("alarmSounded").set(True)

# Start only one instance of this correlator for the system. Note that in this example data is stored in a plugin,
# not in this correlator.
class LatestNewsCorrelator(Correlator):
    instanceStarted = AtomicBoolean(False)
    def onConfigure(self):
        self.withEvent("news")
    def onAcceptAsFirst(self, event):
        return LatestNewsCorrelator.instanceStarted.compareAndSet(False, True)
    def onInit(self):
        storagePlugin.storedValue = collections.deque(maxlen=int(sponge.getVariable("latestNewsMaxSize", 2)))
    def onEvent(self, event):
        storagePlugin.storedValue.append(event.get("title"))
        self.logger.debug("{} - latest news: {}", self.hashCode(), str(storagePlugin.storedValue))

