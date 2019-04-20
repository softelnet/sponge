# Sponge Knowledge base
# Using knowledge base callbacks.

java_import java.util.concurrent.atomic.AtomicBoolean
java_import java.util.concurrent.atomic.AtomicInteger
java_import org.openksavi.sponge.test.util.TestStatus

class ReloadTrigger < Trigger
    def onConfigure
        self.withEvent("reload")
    end

    def onRun(event)
        self.logger.debug("Received event: {}", event.name)
        # $sponge.requestReload()
        $sponge.reload()
    end
end

def onInit
    # Variables for assertions only
    $sponge.setVariable("onInitCalled", AtomicBoolean.new(false))
    $sponge.setVariable("onBeforeLoadCalled", AtomicInteger.new(0))
    $sponge.setVariable("onLoadCalled", AtomicInteger.new(0))
    $sponge.setVariable("onAfterLoadCalled", AtomicInteger.new(0))
    $sponge.setVariable("onStartupCalled", AtomicBoolean.new(false))
    $sponge.setVariable("onBeforeReloadCalled", AtomicBoolean.new(false))
    $sponge.setVariable("onAfterReloadCalled", AtomicBoolean.new(false))

    $sponge.logger.debug("onInit")
    $sponge.getVariable("onInitCalled").set(true)
end

def onBeforeLoad
    $sponge.logger.debug("onBeforeLoad")
    $sponge.getVariable("onBeforeLoadCalled").incrementAndGet()
end

def onLoad
    $sponge.logger.debug("onLoad")
    $sponge.getVariable("onLoadCalled").incrementAndGet()
end

def onAfterLoad
    $sponge.logger.debug("onAfterLoad")
    $sponge.getVariable("onAfterLoadCalled").incrementAndGet()
end

def onStartup
    $sponge.logger.debug("onStartup")
    $sponge.getVariable("onStartupCalled").set(true)
    $sponge.event("reload").sendAfter(1000)
end

def onShutdown
    $sponge.logger.debug("onShutdown")
    # Using Java static field because all variables will be lost after shutdown .
    TestStatus.onShutdownCalled = true
end

def onBeforeReload
    $sponge.logger.debug("onBeforeReload")
    $sponge.getVariable("onBeforeReloadCalled").set(true)
end

def onAfterReload
    $sponge.logger.debug("onAfterReload")
    $sponge.getVariable("onAfterReloadCalled").set(true)
end
