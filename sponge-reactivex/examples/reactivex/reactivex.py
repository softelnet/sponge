"""
Sponge Knowledge base
ReactiveX
"""

import time
from io.reactivex.schedulers import Schedulers

def onStartup():
    EPS.event("e1").set("payload", 1).send()
    EPS.event("e2").set("payload", 2).sendAfter(500)
    EPS.event("e3").set("payload", 3).sendAfter(1000)

    rx.observable.subscribe(lambda event: EPS.logger.info("{}", event.name))

    def observer(event):
        time.sleep(1)
        EPS.logger.info("After sleep: {}", event.name)
    rx.observable.observeOn(Schedulers.io()).subscribe(observer)
