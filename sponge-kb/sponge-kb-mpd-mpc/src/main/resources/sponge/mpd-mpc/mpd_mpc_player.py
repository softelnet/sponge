"""
Sponge Knowledge Base
MPD player.
"""

import os

class MpdPlayer(Action):
    def onConfigure(self):
        self.withLabel("Player").withDescription("The MPD player.")
        self.withArgs([
            StringType("song").withLabel("Song").withNullable().withReadOnly().withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue()),
            StringType("album").withLabel("Album").withNullable().withReadOnly().withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue()),
            StringType("date").withLabel("Date").withNullable().withReadOnly().withProvided(
                ProvidedMeta().withValue()),
            IntegerType("position").withLabel("Position").withNullable().withAnnotated().withMinValue(0).withMaxValue(100).withFeatures(
                {"widget":"slider", "group":"position"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            StringType("time").withLabel("Time").withNullable().withReadOnly().withFeatures({"group":"position"}).withProvided(
                ProvidedMeta().withValue()),


            VoidType("prev").withLabel("Previous").withAnnotated().withFeatures({
                "icon":IconInfo().withName("skip-previous").withSize(30), "group":"navigation", "align":"center"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            BooleanType("play").withLabel("Play").withAnnotated().withFeatures({"group":"navigation"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            VoidType("next").withLabel("Next").withAnnotated().withFeatures({"icon":IconInfo().withName("skip-next").withSize(30), "group":"navigation"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()),

            IntegerType("volume").withLabel("Volume").withAnnotated().withMinValue(0).withMaxValue(100).withFeatures({"widget":"slider"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),

            BooleanType("repeat").withLabel("Repeat").withAnnotated().withFeatures({"group":"mode", "widget":"toggleButton", "icon":"repeat", "align":"right"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            BooleanType("single").withLabel("Single").withAnnotated().withFeatures({"group":"mode", "widget":"toggleButton", "icon":"numeric-1"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            BooleanType("random").withLabel("Random").withAnnotated().withFeatures({"group":"mode", "widget":"toggleButton", "icon":"mixer"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate()),
            BooleanType("consume").withLabel("Consume").withAnnotated().withFeatures({"group":"mode", "widget":"toggleButton", "icon":"pac-man"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable().withLazyUpdate())
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

    def __ensureStatus(self, mpc, status):
        return status if mpc.isStatusOk(status) else mpc.getStatus()

    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        status = None
        (position, size) = (None, None)

        mpc.lock.lock()
        try:
            try:
                if "position" in context.submit:
                    if context.current["position"]:
                        status = mpc.seekByPercentage(context.current["position"].value)
                if "volume" in context.submit:
                        status = mpc.setVolume(context.current["volume"].value)
                if "play" in context.submit:
                    status = mpc.togglePlay(context.current["play"].value)
                if "prev" in context.submit:
                    status = mpc.prev()
                if "next" in context.submit:
                    status = mpc.next()

                if "repeat" in context.submit:
                    status = mpc.setMode("repeat", context.current["repeat"].value)
                if "single" in context.submit:
                    status = mpc.setMode("single", context.current["single"].value)
                if "random" in context.submit:
                    status = mpc.setMode("random", context.current["random"].value)
                if "consume" in context.submit:
                    status = mpc.setMode("consume", context.current["consume"].value)
            except:
                sponge.logger.warn("Submit error: {}", sys.exc_info()[1])

            currentSong = None
            if "song" in context.provide or "date" in context.provide:
                currentSong = mpc.getCurrentSong()
            if "song" in context.provide:
                context.provided["song"] = ProvidedValue().withValue(mpc.getSongLabel(currentSong) if currentSong else None)
            if "album" in context.provide:
                context.provided["album"] = ProvidedValue().withValue(currentSong["album"] if currentSong else None)
            if "date" in context.provide:
                context.provided["date"] = ProvidedValue().withValue(currentSong["date"] if currentSong else None)
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
                playing = mpc.getPlay(status)
                context.provided["play"] = ProvidedValue().withValue(AnnotatedValue(playing).withFeature("icon",
                        IconInfo().withName("pause" if playing else "play").withSize(60)))

            if "prev" in context.provide or "next" in context.provide:
                status = self.__ensureStatus(mpc, status)
                (position, size) = mpc.getCurrentPlaylistPositionAndSize(status)
            if "prev" in context.provide:
                context.provided["prev"] = ProvidedValue().withValue(AnnotatedValue(None).withFeature("enabled", position is not None))
            if "next" in context.provide:
                context.provided["next"] = ProvidedValue().withValue(AnnotatedValue(None).withFeature("enabled",
                        position is not None and size is not None))

            if "repeat" in context.provide or "single" in context.provide or "random" in context.provide or "consume" in context.provide:
                status = self.__ensureStatus(mpc, status)
                modes = mpc.getModes(status)

                if "repeat" in context.provide:
                    context.provided["repeat"] = ProvidedValue().withValue(AnnotatedValue(modes.get("repeat", False)))
                if "single" in context.provide:
                    context.provided["single"] = ProvidedValue().withValue(AnnotatedValue(modes.get("single", False)))
                if "random" in context.provide:
                    context.provided["random"] = ProvidedValue().withValue(AnnotatedValue(modes.get("random", False)))
                if "consume" in context.provide:
                    context.provided["consume"] = ProvidedValue().withValue(AnnotatedValue(modes.get("consume", False)))
        finally:
            mpc.lock.unlock()
