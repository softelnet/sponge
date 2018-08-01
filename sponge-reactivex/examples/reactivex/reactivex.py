"""
Sponge Knowledge base
ReactiveX
"""

import time
from io.reactivex.schedulers import Schedulers

def onStartup():
    sponge.event("e1").set("payload", 1).send()
    sponge.event("e2").set("payload", 2).sendAfter(500)
    sponge.event("e3").set("payload", 3).sendAfter(1000)

    rx.observable.subscribe(lambda event: sponge.logger.info("{}", event.name))

    def observer(event):
        time.sleep(1)
        sponge.logger.info("After sleep: {}", event.name)
    rx.observable.observeOn(Schedulers.io()).subscribe(observer)
