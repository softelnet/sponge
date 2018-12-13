/**
 * Sponge Knowledge base
 * Hello World action
 */

var HelloWorldAction = Java.extend(Action, {
    onConfigure: function(self) {
        self.displayName = "Hello world";
        self.description = "Returns a greeting text.";
        self.argsMeta = [new ArgMeta("name", new StringType()).displayName("Your name").description("Type your name.")];
        self.resultMeta = new ResultMeta(new StringType()).displayName("Greeting").description("The greeting text.");
    },
    onCall: function(self, args) {
        // The onCall method in JS always gets an array of arguments. Dynamic onCall callback methods are not supported.
        return "Hello World! Hello " + args[0] + "!";
    }
});

function onStartup() {
    sponge.logger.info("{}", sponge.call("HelloWorldAction", ["Sponge user"]))
}
