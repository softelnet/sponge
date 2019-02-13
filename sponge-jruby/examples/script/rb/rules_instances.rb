# Sponge Knowledge base
# Rules - instances

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $sponge.setVariable("countA", AtomicInteger.new(0))
    $sponge.setVariable("countB", AtomicInteger.new(0))
    $sponge.setVariable("max", 100)
end

class RuleA < Rule
    def onConfigure
        self.withEvents(["a a1", "a a2"])
    end
    def onRun(event)
        $sponge.getVariable("countA").incrementAndGet()
    end
end

class RuleB < Rule
    def onConfigure
        self.withEvents(["b b1", "b b2"])
    end
    def onRun(event)
        $sponge.getVariable("countB").incrementAndGet()
    end
end

def onStartup
    for i in (0...$sponge.getVariable("max"))
        $sponge.event("a").send()
        $sponge.event("b").send()
    end
end