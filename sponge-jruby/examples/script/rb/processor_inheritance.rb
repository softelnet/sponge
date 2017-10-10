# Sponge Knowledge base
# Processor inheritance

def onInit
    # Variables for assertions only
    $EPS.setVariable("result", nil)
end

class AbstractEchoAction < Action
    def calculateResult(args)
        return 1
    end
end

class EchoAction < AbstractEchoAction
    def onCall(args)
        return calculateResult(args) * 2
    end
end

def onStartup
    result = $EPS.call("EchoAction")
    $EPS.setVariable("result", result)
    $EPS.logger.debug("Action returned: {}", result)
end
