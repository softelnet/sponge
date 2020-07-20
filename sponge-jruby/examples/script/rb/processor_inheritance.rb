# Sponge Knowledge Base
# Processor inheritance

def onInit
    # Variables for assertions only
    $sponge.setVariable("result", nil)
end

class AbstractEchoAction < Action
    def calculateResult()
        return 1
    end
end

class EchoAction < AbstractEchoAction
    def onCall()
        return calculateResult() * 2
    end
end

def onStartup
    result = $sponge.call("EchoAction")
    $sponge.setVariable("result", result)
    $sponge.logger.debug("Action returned: {}", result)
end
