# Sponge Knowledge base
# Defining, calling and disabling Actions

java_import org.openksavi.sponge.examples.PowerEchoAction

def onInit
    # Variables for assertions only
    $EPS.setVariable("scriptActionResult", nil)
    $EPS.setVariable("javaActionResult", nil)
end

class EchoAction < Action
    def onConfigure
        self.displayName = "Echo Action"
    end
    def onCall(args)
        self.logger.info("Action {} called", self.name)
        for arg in args
            self.logger.debug("Arg: {} ({})", arg, arg.class.superclass)
        end
    	return args
    end
end

def onLoad
    $EPS.enableJava(PowerEchoAction)
end

def onStartup
    $EPS.logger.debug("Calling script defined action")
    scriptActionResult = $EPS.call("EchoAction", 1, "test")
    $EPS.logger.debug("Action returned: {}", scriptActionResult)
    $EPS.setVariable("scriptActionResult", scriptActionResult)

    $EPS.logger.debug("Calling Java defined action")
    javaActionResult = $EPS.call("PowerEchoAction", 1, "test")
    $EPS.logger.debug("Action returned: {}", javaActionResult)
    $EPS.setVariable("javaActionResult", javaActionResult)

    $EPS.logger.debug("Disabling actions")
    $EPS.disable(EchoAction)
    $EPS.disableJava(PowerEchoAction)
end
