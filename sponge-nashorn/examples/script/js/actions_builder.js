/**
 * Sponge Knowledge base
 * Action builders
 */

function onInit() {
    // Variables for assertions only
    sponge.setVariable("initialized_UpperEchoAction", false)
    sponge.setVariable("called_UpperEchoActionMulti",  false)
    sponge.setVariable("called_NoArgAndResultAction",  false)
}

function onLoad() {
    sponge.enable(new ActionBuilder("UpperEchoAction").withLabel("Echo Action").withDescription("Returns the upper case string")
            .withArg(new StringType("text").withLabel("Argument 1").withDescription("Argument 1 description"))
            .withResult(new StringType().withLabel("Upper case string").withDescription("Result description"))
            .withOnInit(function(action) {
                sponge.setVariable("initialized_" + action.meta.name, true)
            })
            .withOnCall(function(action, text) {
                return text.toUpperCase()
            }))

    sponge.enable(new ActionBuilder("UpperEchoActionMulti").withLabel("Echo Action").withDescription("Returns the upper case string")
            .withArg(new StringType("text").withLabel("Argument 1").withDescription("Argument 1 description"))
            .withResult(new StringType().withLabel("Upper case string").withDescription("Result description"))
            .withOnCallArgs(function (action, args) {
                sponge.logger.info("Action {} called", action.meta.name)
                sponge.setVariable("called_" + action.meta.name, true)
                return args[0].toUpperCase()
            }))

    sponge.enable(new ActionBuilder("NoArgAndResultAction").withOnCall(function (action) {
        sponge.setVariable("called_" + action.meta.name, true)
    }))

    sponge.enable(new ActionBuilder("ProvidedArgsAction").withArg(new StringType("text").withProvided(new ProvidedMeta().withValue())).withNonCallable()
            .withOnProvideArgs(function (action, context) {
                if (context.provide.has("text")) {
                    context.provided["text"] = new ProvidedValue().withValue("ABC")
                }
            }))
}


