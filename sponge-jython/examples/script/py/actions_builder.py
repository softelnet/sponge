"""
Sponge Knowledge base
Action builders
"""

def onInit():
    sponge.setVariable("initialized_UpperEchoAction", False)
    sponge.setVariable("called_UpperEchoActionMulti", False)
    sponge.setVariable("called_NoArgAndResultAction", False)

def onLoad():
    sponge.enable(ActionBuilder("UpperEchoAction").withLabel("Echo Action").withDescription("Returns the upper case string")
                  .withArg(StringType("text").withLabel("Argument 1").withDescription("Argument 1 description"))
                  .withResult(StringType().withLabel("Upper case string").withDescription("Result description"))
                  .withOnInit(lambda action: sponge.setVariable("initialized_" + action.meta.name, True))
                  .withOnCall(lambda action, text: text.upper()))

    def multiOnCall(action, args):
        sponge.logger.info("Action {} called", action.meta.name)
        sponge.setVariable("called_" + action.meta.name, True)
        return args[0].upper()

    sponge.enable(ActionBuilder("UpperEchoActionMulti").withLabel("Echo Action").withDescription("Returns the upper case string")
                  .withArg(StringType("text").withLabel("Argument 1").withDescription("Argument 1 description"))
                  .withResult(StringType().withLabel("Upper case string").withDescription("Result description"))
                  .withOnCallArgs(multiOnCall))

    sponge.enable(ActionBuilder("NoArgAndResultAction").withOnCall(lambda action: sponge.setVariable("called_" + action.meta.name, True)))

    def onProvideArgs(action, context):
        if "text" in context.provide:
            context.provided["text"] = ProvidedValue().withValue("ABC")

    sponge.enable(ActionBuilder("ProvidedArgsAction").withArg(StringType("text").withProvided(ProvidedMeta().withValue())).withNonCallable()
                  .withOnProvideArgs(onProvideArgs))
