/*
 * Sponge Knowledge Base
 * Triggers - Incorrect event pattern
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger

class TriggersEventPatternIncorrect : KKnowledgeBase() {

    class TriggerAPattern : KTrigger() {
        override fun onConfigure() {
            withEvent("a.**")
        }
        override fun onRun(event: Event) {
        }
    }
}
