# Sponge Knowledge base
# Actions - Java inheritance

java_import org.openksavi.sponge.examples.PowerEchoAction

class ExtendedFromAction < PowerEchoAction
    def onCall(value, text)
        return [value + 10, text.downcase]
    end
end
