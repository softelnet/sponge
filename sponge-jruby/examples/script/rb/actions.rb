# Sponge Knowledge Base
# Defining, calling and disabling Actions

java_import org.openksavi.sponge.examples.PowerEchoAction

def onInit
    # Variables for assertions only
    $sponge.setVariable("scriptActionResult", nil)
    $sponge.setVariable("javaActionResult", nil)
end

class EchoAction < Action
    def onConfigure
        self.withLabel("Echo Action")
    end
    def onCall(value, text)
        self.logger.info("Action {} called", self.meta.name)
        return [value, text]
    end
end

class ArrayArgumentAction < Action
    def onCall(arrayArg)
        return arrayArg.length
    end
end

def onLoad
    $sponge.enableJava(PowerEchoAction)
end

def onStartup
    $sponge.logger.debug("Calling script defined action")
    scriptActionResult = $sponge.call("EchoAction", [1, "test"])
    $sponge.logger.debug("Action returned: {}", scriptActionResult)
    $sponge.setVariable("scriptActionResult", scriptActionResult)

    $sponge.logger.debug("Calling Java defined action")
    javaActionResult = $sponge.call("PowerEchoAction", [1, "test"])
    $sponge.logger.debug("Action returned: {}", javaActionResult)
    $sponge.setVariable("javaActionResult", javaActionResult)

    $sponge.logger.debug("Disabling actions")
    $sponge.disable(EchoAction)
    $sponge.disableJava(PowerEchoAction)
end
