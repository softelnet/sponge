/*
 * Sponge Knowledge base
 * Triggers - Incorrect event pattern
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KTrigger

class TriggersEventPatternIncorrect : KKnowledgeBase() {

    class TriggerAPattern : KTrigger() {
        override fun onConfigure() = setEvent("a.**")
        override fun onRun(event: Event) {
        }
    }
}
