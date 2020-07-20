/**
 * Sponge Knowledge Base
 * Actions - Java inheritance
 */

import org.openksavi.sponge.examples.PowerEchoAction

class ExtendedFromAction extends PowerEchoAction {
    Object onCall(int value, String text) {
        return [value + 10, text.toLowerCase()]
    }
}

void onStartup() {
}
