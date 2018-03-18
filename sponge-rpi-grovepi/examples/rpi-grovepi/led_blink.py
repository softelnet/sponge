"""
Sponge Knowledge base
Blinking led
GrovePi board: Connect led to D4
"""

state = False

class LedBlink(Trigger):
    def onConfigure(self):
        self.event = "blink"
    def onRun(self, event):
        global led, state
        state = not state
        led.set(state)

def onStartup():
    global led
    led = grovepi.device.getDigitalOut(4)
    EPS.event("blink").sendAfter(0, 1000)

def onShutdown():
    off()

on = lambda: led.set(True)
off = lambda: led.set(False)