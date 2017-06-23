# Sponge Knowledge base
# Using knowledge base callbacks.

java_import java.util.concurrent.atomic.AtomicBoolean
java_import java.util.concurrent.atomic.AtomicInteger
java_import org.openksavi.sponge.examples.TestStatus

class ReloadTrigger < Trigger
    def configure
        self.eventName = "reload"
    end

    def run(event)
        self.logger.debug("Received event: {}", event.name)
        # $EPS.requestReload()
        $EPS.reload()
    end
end

def onInit
    # Variables for assertions only
    $EPS.setVariable("onInitCalled", AtomicBoolean.new(false))
    $EPS.setVariable("onLoadCalled", AtomicInteger.new(0))
    $EPS.setVariable("onStartupCalled", AtomicBoolean.new(false))
    $EPS.setVariable("onBeforeReloadCalled", AtomicBoolean.new(false))
    $EPS.setVariable("onAfterReloadCalled", AtomicBoolean.new(false))

    $EPS.logger.debug("onInit")
    $EPS.getVariable("onInitCalled").set(true)
end

def onLoad
    $EPS.logger.debug("onLoad")
    $EPS.getVariable("onLoadCalled").incrementAndGet()
end

def onStartup
    $EPS.logger.debug("onStartup")
    $EPS.getVariable("onStartupCalled").set(true)
    $EPS.event("reload").sendAfter(1000)
end

def onShutdown
    $EPS.logger.debug("onShutdown")
    # Using Java static field because all variables will be lost after shutdown .
    TestStatus.onShutdownCalled = true
end

def onBeforeReload
    $EPS.logger.debug("onBeforeReload")
    $EPS.getVariable("onBeforeReloadCalled").set(true)
end

def onAfterReload
    $EPS.logger.debug("onAfterReload")
    $EPS.getVariable("onAfterReloadCalled").set(true)
end
