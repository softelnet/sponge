"""
Sponge Knowledge Base
MPD player.
"""

class MpdPlayer(Action):
    def onConfigure(self):
        self.withLabel("Player").withDescription("The MPD player.")
        self.withArgs([
            # The song info arguments.
            StringType("song").withLabel("Song").withNullable().withReadOnly()
                .withProvided(ProvidedMeta().withValue()),
            StringType("album").withLabel("Album").withNullable().withReadOnly()
                .withProvided(ProvidedMeta().withValue()),
            StringType("date").withLabel("Date").withNullable().withReadOnly()
                .withProvided(ProvidedMeta().withValue()),

            # The position arguments.
            IntegerType("position").withLabel("Position").withNullable().withAnnotated()
                .withMinValue(0).withMaxValue(100)
                .withFeatures({"widget":"slider", "group":"position"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            StringType("time").withLabel("Time").withNullable().withReadOnly()
                .withFeatures({"group":"position"}).withProvided(ProvidedMeta().withValue()),

            # The navigation arguments.
            VoidType("prev").withLabel("Previous").withAnnotated()
                .withFeatures({"icon":IconInfo().withName("skip-previous").withSize(30),
                               "group":"navigation", "align":"center"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            BooleanType("play").withLabel("Play").withAnnotated().withFeatures({"group":"navigation"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            VoidType("next").withLabel("Next").withAnnotated()
                .withFeatures({"icon":IconInfo().withName("skip-next").withSize(30), "group":"navigation"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable()),

            # The volume argument.
            IntegerType("volume").withLabel("Volume").withAnnotated().withMinValue(0).withMaxValue(100)
                .withFeatures({"widget":"slider"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),

            # The mode arguments.
            BooleanType("repeat").withLabel("Repeat").withAnnotated()
                .withFeatures({"group":"mode", "widget":"toggleButton", "icon":"repeat", "align":"right"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            BooleanType("single").withLabel("Single").withAnnotated()
                .withFeatures({"group":"mode", "widget":"toggleButton", "icon":"numeric-1"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            BooleanType("random").withLabel("Random").withAnnotated()
                .withFeatures({"group":"mode", "widget":"toggleButton", "icon":"shuffle"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            BooleanType("consume").withLabel("Consume").withAnnotated()
                .withFeatures({"group":"mode", "widget":"toggleButton", "icon":"pac-man"})
                .withProvided(ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate())
        ]).withNonCallable().withActivatable()
        self.withFeatures({"refreshEvents":["statusPolling", "mpdNotification_.*"], "icon":"music", "contextActions":[
            SubAction("MpdPlaylist"),
            SubAction("MpdFindAndAddToPlaylist"),
            SubAction("ViewSongInfo"),
            SubAction("ViewSongLyrics"),
            SubAction("MpdLibrary"),
            SubAction("ViewMpdStatus"),
        ]})

    def onIsActive(self, context):
        return sponge.getVariable("mpc").isConnected()

    def onProvideArgs(self, context):
        """This callback method:
        a) Modifies the MPD state by using the argument values submitted by the user. The names
        are specified in the context.submit set, the values are stored in the context.current map.
        b) Sets the values of arguments that are to be provided to the client. The names are
        specified in the context.provide set, the values are to be stored in the context.provided map.
        """
        MpdPlayerProvideArgsRuntime(context).run()

