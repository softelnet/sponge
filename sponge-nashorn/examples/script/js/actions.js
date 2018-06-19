/**
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

function onInit() {
    // Variables for assertions only
    EPS.setVariable("scriptActionResult", null);
    EPS.setVariable("javaActionResult",  null);
}

var EchoAction = Java.extend(Action, {
    onConfigure: function(self) {
        self.displayName = "Echo Action";
    },
    onCall: function(self, args) {
        self.logger.info("Action {} called", self.name);
        for each (var arg in args) {
            self.logger.debug("Arg: {} ({})", arg, typeof arg);
        }
    	return args;
    }
});

var ArrayArgumentAction = Java.extend(Action, {
    onCall: function(self, arrayArg) {
        // Dynamic onCall limitation for JavaScript.
        return arrayArg[0].length;
    }
});

function onLoad() {
    EPS.enableJava(org.openksavi.sponge.examples.PowerEchoAction.class);
}

function onStartup() {
    EPS.logger.debug("Calling script defined action");
    EPS.setVariable("scriptActionResult", EPS.call("EchoAction", 1, "test"));
    EPS.logger.debug("Action returned: {}", EPS.getVariable("scriptActionResult"));

    EPS.logger.debug("Calling Java defined action");
    EPS.setVariable("javaActionResult", EPS.call("PowerEchoAction", 1, "test"));
    EPS.logger.debug("Action returned: {}", EPS.getVariable("javaActionResult"));

    EPS.logger.debug("Disabling actions");
    EPS.disable(EchoAction);
    EPS.disableJava(org.openksavi.sponge.examples.PowerEchoAction.class);
}

