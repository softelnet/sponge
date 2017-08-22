# Sponge Knowledge base
# Rules - instances

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $EPS.setVariable("countA", AtomicInteger.new(0))
    $EPS.setVariable("countB", AtomicInteger.new(0))
    $EPS.setVariable("max", 100)
end

class RuleA < Rule
    def onConfigure
        self.events = ["a a1", "a a2"]
    end
    def onRun(event)
        $EPS.getVariable("countA").incrementAndGet()
    end
end

class RuleB < Rule
    def onConfigure
        self.events = ["b b1", "b b2"]
    end
    def onRun(event)
        $EPS.getVariable("countB").incrementAndGet()
    end
end

def onStartup
    for i in (0...$EPS.getVariable("max"))
        $EPS.event("a").send()
        $EPS.event("b").send()
    end
end