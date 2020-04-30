# Sponge Knowledge base
# Action builders

def onInit
    # Variables for assertions only
    $sponge.setVariable("initialized_UpperEchoAction", false)
    $sponge.setVariable("called_UpperEchoActionMulti", false)
    $sponge.setVariable("called_NoArgAndResultAction", false)
end

def onLoad
    $sponge.enable(ActionBuilder.new("UpperEchoAction").withLabel("Echo Action").withDescription("Returns the upper case string")
                  .withArg(StringType.new("text").withLabel("Argument 1").withDescription("Argument 1 description"))
                  .withResult(StringType.new().withLabel("Upper case string").withDescription("Result description"))
                  .withOnInit {|action| $sponge.setVariable("initialized_" + action.meta.name, true)}
                  .withOnCall {|action, text| text.upcase})

    $sponge.enable(ActionBuilder.new("UpperEchoActionMulti").withLabel("Echo Action").withDescription("Returns the upper case string")
                  .withArg(StringType.new("text").withLabel("Argument 1").withDescription("Argument 1 description"))
                  .withResult(StringType.new().withLabel("Upper case string").withDescription("Result description"))
                  .withOnCallArgs do |action, args|
                      $sponge.logger.info("Action {} called", action.meta.name)
                      $sponge.setVariable("called_" + action.meta.name, true)
                      args[0].upcase
                  end)

    $sponge.enable(ActionBuilder.new("NoArgAndResultAction").withOnCall {|action| $sponge.setVariable("called_" + action.meta.name, true)} )

    $sponge.enable(ActionBuilder.new("ProvidedArgsAction").withArg(StringType.new("text").withProvided(ProvidedMeta.new().withValue())).withNonCallable()
        .withOnProvideArgs do |action, context|
            if context.provide === "text"
                context.provided.put("text", ProvidedValue.new().withValue("ABC"))
            end
        end)
end
