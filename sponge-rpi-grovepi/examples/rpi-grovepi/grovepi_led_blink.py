"""
Sponge Knowledge Base
Blinking LED
GrovePi board: Connect LED to D4
"""

state = False
led = None

class LedBlink(Trigger):
    def onConfigure(self):
        self.withEvent("blink")
    def onRun(self, event):
        global led, state
        state = not state
        led.set(state)

def onStartup():
    global led
    led = grovepi.device.getDigitalOut(4)
    sponge.event("blink").sendAfter(0, 1000)

def onShutdown():
    global led
    if led is not None:
        led.set(False)
