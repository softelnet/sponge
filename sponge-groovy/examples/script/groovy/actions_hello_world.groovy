/**
 * Sponge Knowledge base
 * Hello World action
 */

class HelloWorldAction extends Action {
    void onConfigure() {
        this.displayName = "Hello world"
        this.description = "Returns a greeting text."
        this.argsMeta = [new ArgMeta("name", new StringType()).displayName("Your name").description("Type your name.")]
        this.resultMeta = new ResultMeta(new StringType()).displayName("Greeting").description("The greeting text.")
    }

    String onCall(String name) {
        return "Hello World! Hello $name!"
    }
}

void onStartup() {
    sponge.logger.info("{}", sponge.call("HelloWorldAction", "Sponge user"))
}
