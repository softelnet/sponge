"""
Sponge Knowledge base
MPD player.
"""

class MpdPlayer(Action):
    def onConfigure(self):
        self.withLabel("Player").withDescription("The MPD player.")
        self.withArgs([
            StringType("song").withLabel("Song").withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue().withReadOnly()),
            IntegerType("position").withLabel("Position").withAnnotated().withMinValue(0).withMaxValue(100).withFeatures({"widget":"slider"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            StringType("time").withLabel("Time").withNullable().withProvided(
                ProvidedMeta().withValue().withReadOnly()),
            IntegerType("volume").withLabel("Volume").withAnnotated().withMinValue(0).withMaxValue(100).withFeatures({"widget":"slider"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            VoidType("prev").withLabel("Previous").withProvided(ProvidedMeta().withSubmittable()),
            BooleanType("play").withLabel("Play").withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()).withFeatures({"widget":"switch"}),
            VoidType("next").withLabel("Next").withProvided(ProvidedMeta().withSubmittable())
        ]).withNoResult().withCallable(False)
        self.withFeatures({"clearLabel":None, "cancelLabel":"Close", "refreshLabel":None, "refreshEvents":["statusPolling", "mpdNotification_.*"],
                           "icon":"music"})
        self.withFeature("contextActions", [
            "MpdPlaylist()", "MpdSetAndPlayPlaylist()", "ViewSongLyrics()", "ViewMpdStatus()",
        ])

    def __ensureStatus(self, mpc, status):
        return status if mpc.isStatusOk(status) else mpc.getStatus()

    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        status = None

        mpc.lock.lock()
        try:
            if "position" in context.submit:
                status = mpc.seekByPercentage(context.current["position"].value)
            if "volume" in context.submit:
                status = mpc.setVolume(context.current["volume"].value)
            if "play" in context.submit:
                status = mpc.togglePlay(context.current["play"])
            if "prev" in context.submit:
                status = mpc.prev()
            if "next" in context.submit:
                status = mpc.next()

            if "song" in context.provide:
                context.provided["song"] = ProvidedValue().withValue(mpc.getCurrentSong())
            if "position" in context.provide or "context" in context.submit:
                status = self.__ensureStatus(mpc, status)
                context.provided["position"] = ProvidedValue().withValue(AnnotatedValue(mpc.getPositionByPercentage(status)).withFeature(
                    "enabled", mpc.isStatusPlayingOrPaused(status)))
            if "time" in context.provide:
                status = self.__ensureStatus(mpc, status)
                context.provided["time"] = ProvidedValue().withValue(mpc.getTimeStatus(status))
            # Provide an annotated volume value at once if submitted.
            if "volume" in context.provide or "volume" in context.submit:
                status = self.__ensureStatus(mpc, status)
                volume = mpc.getVolume(status)
                context.provided["volume"] = ProvidedValue().withValue(AnnotatedValue(volume).withTypeLabel(
                    "Volume" + ((" (" + str(volume) + "%)") if volume else "")))
            if "play" in context.provide:
                status = self.__ensureStatus(mpc, status)
                context.provided["play"] = ProvidedValue().withValue(mpc.getPlay(status))
        finally:
            mpc.lock.unlock()
