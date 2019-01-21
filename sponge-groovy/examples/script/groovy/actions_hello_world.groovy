/**
 * Sponge Knowledge base
 * Hello World action
 */

class HelloWorldAction extends Action {
    void onConfigure() {
        this.label = "Hello world"
        this.description = "Returns a greeting text."
        this.argsMeta = [new ArgMeta("name", new StringType()).label("Your name").description("Type your name.")]
        this.resultMeta = new ResultMeta(new StringType()).label("Greeting").description("The greeting text.")
    }

    String onCall(String name) {
        return "Hello World! Hello $name!"
    }
}

void onStartup() {
    sponge.logger.info("{}", sponge.call("HelloWorldAction", ["Sponge user"]))
}
