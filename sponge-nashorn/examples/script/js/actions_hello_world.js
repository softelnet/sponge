/**
 * Sponge Knowledge base
 * Hello World action
 */

var HelloWorldAction = Java.extend(Action, {
    onConfigure: function(self) {
        self.withLabel("Hello world").withDescription("Returns a greeting text.");
        self.withArg(new ArgMeta("name", new StringType()).withLabel("Your name").withDescription("Type your name."));
        self.withResult(new ResultMeta(new StringType()).withLabel("Greeting").withDescription("The greeting text."));
    },
    onCall: function(self, args) {
        // The onCall method in JS always gets an array of arguments. Dynamic onCall callback methods are not supported.
        return "Hello World! Hello " + args[0] + "!";
    }
});

function onStartup() {
    sponge.logger.info("{}", sponge.call("HelloWorldAction", ["Sponge user"]))
}
