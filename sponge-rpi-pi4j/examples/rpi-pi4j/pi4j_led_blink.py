"""
Sponge Knowledge base
Blinking LED
Raspberry Pi: Make sure that your Raspberry Pi is not powered! Then connect Grove LED to GPIO via a 4-pin connector:
- BLACK wire goes on PIN#14 (Ground),
- RED wire goes on PIN#02 (DC Power 5V),
- YELLOW wire goes on PIN#12 (GPIO18/GPIO_GEN1),
- WHITE wire goes on PIN#06 (Ground).
"""

from com.pi4j.io.gpio import RaspiPin, PinState

state = False

class LedBlink(Trigger):
    def onConfigure(self):
        self.event = "blink"
    def onRun(self, event):
        global led, state
        state = not state
        led.setState(state)

def onStartup():
    global led
    led = pi.gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "led", PinState.LOW)
    sponge.event("blink").sendAfter(0, 1000)

def onShutdown():
    global led
    if led is not None:
        led.setState(state)
