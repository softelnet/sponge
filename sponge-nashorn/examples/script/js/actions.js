/**
 * Sponge Knowledge base
 * Defining, calling and disabling Actions
 */

function onInit() {
    // Variables for assertions only
    sponge.setVariable("scriptActionResult", null);
    sponge.setVariable("javaActionResult",  null);
}

var EchoAction = Java.extend(Action, {
    onConfigure: function(self) {
        self.label = "Echo Action";
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
    sponge.enableJava(org.openksavi.sponge.examples.PowerEchoAction.class);
}

function onStartup() {
    sponge.logger.debug("Calling script defined action");
    sponge.setVariable("scriptActionResult", sponge.call("EchoAction", [1, "test"]));
    sponge.logger.debug("Action returned: {}", sponge.getVariable("scriptActionResult"));

    sponge.logger.debug("Calling Java defined action");
    sponge.setVariable("javaActionResult", sponge.call("PowerEchoAction", [1, "test"]));
    sponge.logger.debug("Action returned: {}", sponge.getVariable("javaActionResult"));

    sponge.logger.debug("Disabling actions");
    sponge.disable(EchoAction);
    sponge.disableJava(org.openksavi.sponge.examples.PowerEchoAction.class);
}

