"""
Sponge Knowledge Base
MPD player runtime.
"""

class MpdPlayerProvideArgsRuntime:
    def __init__(self, context):
        self.context = context

        # An instance of the Mpc class that is a wrapper for the mpc (MPD client) commandline calls.
        self.mpc = sponge.getVariable("mpc")

        # Stores and caches the latest MPD status as returned by the mpc command.
        self.status = None

        # Providers for the submittable arguments.
        self.submitProviders = {
            "position":lambda value: self.mpc.seekByPercentage(value),
            "volume":lambda value: self.mpc.setVolume(value),

            "play":lambda value: self.mpc.togglePlay(value),
            "prev":lambda value: self.mpc.prev(),
            "next":lambda value: self.mpc.next(),

            "repeat":lambda value: self.mpc.setMode("repeat", value),
            "single":lambda value: self.mpc.setMode("single", value),
            "random":lambda value: self.mpc.setMode("random", value),
            "consume":lambda value: self.mpc.setMode("consume", value),
            }

        # The MPD mode types (that correspond to the action arguments).
        self.modeTypes = ["repeat", "single" , "random", "consume"]

    def run(self):
        # Enter the critical section.
        self.mpc.lock.lock()
        try:
            # Pass the context as an argument to the private methods in order to simplify the code.

            # Submit arguments sent from the client.
            self.__submitArgs(self.context)

            # Provide arguments to the client.
            self.__provideInfoArgs(self.context)
            self.__providePositionArgs(self.context)
            self.__provideVolumeArg(self.context)
            self.__provideNavigationArgs(self.context)
            self.__provideModeArgs(self.context)
        finally:
            self.mpc.lock.unlock()

    def __submitArgs(self, context):
        """Submits arguments sent from the client by changing the state of the MPD daemon.
        """
        # Use simple providers to submit values.
        for name in context.submit:
            provider = self.submitProviders.get(name)
            if provider:
                try:
                    # Cache the current MPD status.
                    self.status = provider(context.current[name].value if context.current[name] else None)
                except:
                    sponge.logger.warn("Submit error: {}", sys.exc_info()[1])

    def __getStatus(self):
        # Cache the mpc command status text.
        if not self.mpc.isStatusOk(self.status):
            self.status = self.mpc.getStatus()

        return self.status

    def __provideInfoArgs(self, context):
        """Provides the song info arguments to the client.
        """
        # Read the current song from the MPD daemon only if necessary.
        currentSong = self.mpc.getCurrentSong() if any(arg in context.provide for arg in ["song", "album" , "date"]) else None

        if "song" in context.provide:
            context.provided["song"] = ProvidedValue().withValue(
                self.mpc.getSongLabel(currentSong) if currentSong else None)
        if "album" in context.provide:
            context.provided["album"] = ProvidedValue().withValue(
                currentSong["album"] if currentSong else None)
        if "date" in context.provide:
            context.provided["date"] = ProvidedValue().withValue(
                currentSong["date"] if currentSong else None)

    def __providePositionArgs(self, context):
        """Provides the position arguments to the client.
        """
        # If submitted, provide an updated annotated position too.
        if "position" in context.provide or "context" in context.submit:
            context.provided["position"] = ProvidedValue().withValue(
                AnnotatedValue(self.mpc.getPositionByPercentage(self.__getStatus()))
                    .withFeature("enabled", self.mpc.isStatusPlayingOrPaused(self.__getStatus())))
        if "time" in context.provide:
            context.provided["time"] = ProvidedValue().withValue(self.mpc.getTimeStatus(self.__getStatus()))

    def __provideVolumeArg(self, context):
        """Provides the volume argument to the client.
        """
        # If submitted, provide an updated annotated volume too.
        if "volume" in context.provide or "volume" in context.submit:
            volume = self.mpc.getVolume(self.__getStatus())
            context.provided["volume"] = ProvidedValue().withValue(
                AnnotatedValue(volume)
                    .withTypeLabel("Volume" + ((" (" + str(volume) + "%)") if volume else "")))

    def __provideNavigationArgs(self, context):
        """Provides the navigation arguments to the client.
        """
        if "play" in context.provide:
            playing = self.mpc.getPlay(self.__getStatus())
            context.provided["play"] = ProvidedValue().withValue(
                AnnotatedValue(playing).withFeature("icon",
                    IconInfo().withName("pause" if playing else "play").withSize(60)))

        # Read the current playlist position and size from the MPD daemon only if necessary.
        (position, size) = (None, None)
        if "prev" in context.provide or "next" in context.provide:
            (position, size) = self.mpc.getCurrentPlaylistPositionAndSize(self.__getStatus())

        if "prev" in context.provide:
            context.provided["prev"] = ProvidedValue().withValue(
                AnnotatedValue(None).withFeature("enabled", position is not None))
        if "next" in context.provide:
            context.provided["next"] = ProvidedValue().withValue(
                AnnotatedValue(None).withFeature("enabled",
                    position is not None and size is not None))

    def __provideModeArgs(self, context):
        """Provides the mode arguments to the client.
        """
        currentModes = None

        # Provide only required modes (i.e. specified in the context.provide set).
        for arg in [a for a in self.modeTypes if a in context.provide]:
            if currentModes is None:
                # Read the current modes from the MPD daemon only if necessary.
                currentModes = self.mpc.getModes(self.__getStatus())

            context.provided[arg] = ProvidedValue().withValue(
                    AnnotatedValue(currentModes.get(arg, False)))
