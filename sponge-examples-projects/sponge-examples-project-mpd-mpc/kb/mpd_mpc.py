"""
Sponge Knowledge base
Uses mpc (MPD client).
"""

import sys

class MpdSetAndPlayPlaylist(Action):
    def onConfigure(self):
        self.withLabel("Set and play a playlist").withDescription("Sets a playlist according to the arguments and starts playing it immediately. Uses mpc")
        self.withArgs([
            StringType("artist").withNullable().withLabel("Artist").withDescription("The artist"),
            StringType("album").withNullable().withLabel("Album").withDescription("The album"),
            StringType("genre").withNullable().withLabel("Genre").withDescription("The genre"),
            IntegerType("minYear").withNullable().withLabel("Release year (since)").withDescription("The album minimum release year."),
            IntegerType("maxYear").withNullable().withLabel("Release year (to)").withDescription("The album maximum release year."),
            BooleanType("autoPlay").withDefaultValue(True).withLabel("Auto play").withDescription("Plays the playlist automatically.")
        ]).withResult(StringType().withLabel("Info").withDescription("A short info of the status of the action call."))
        self.withFeatures({"icon":"view-headline"})
    def onCall(self, artist, album, genre, minYear, maxYear, autoPlay):
        mpc = sponge.getVariable("mpc")
        sponge.logger.info("Setting the playlist...")
        selectedFiles = mpc.searchFiles(artist, album, genre, minYear, maxYear, useSimpleRegexp = True)
        sponge.logger.info("{} files(s) found".format(len(selectedFiles)))
        if len(selectedFiles) > 0:
            mpc.setAndPlayFiles(selectedFiles, autoPlay)
            return "The playlist is set, {} files(s) found".format(len(selectedFiles))
        else:
            return "No matching files found"

class ViewSongLyrics(Action):
    def onConfigure(self):
        self.withLabel("Song lyrics").withDescription("View the current song lyrics.")
        self.withArgs([
            StringType("song").withLabel("Song").withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue().withReadOnly()),
            StringType("lyrics").withLabel("Lyrics").withProvided(
                ProvidedMeta().withValue().withReadOnly().withDependency("song")),
        ]).withNoResult().withCallable(False)
        self.withFeatures({"icon":"script-text-outline", "clearLabel":None, "cancelLabel":"Close", "refreshLabel":None, "refreshEvents":["mpdNotification"]})
    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        if "song" in context.provide:
            context.provided["song"] = ProvidedValue().withValue(mpc.getCurrentSong())
        if "lyrics" in context.provide:
            try:
                if context.current["song"] and " - " in context.current["song"]:
                    (artist, title) = tuple(context.current["song"].split(" - ", 2)[:2])
                    lyricsService = sponge.getVariable("lyricsService")
                    if lyricsService.configured:
                        lyrics = lyricsService.getLyrics(artist, title)
                    else:
                        lyrics = "LYRICS SERVICE NOT CONFIGURED"
                else:
                    lyrics = ""
            except:
                lyrics = "LYRICS ERROR: " + str(sys.exc_info()[1])

            context.provided["lyrics"] = ProvidedValue().withValue(lyrics)

class ViewMpdStatus(Action):
    def onConfigure(self):
        self.withLabel("MPD status").withDescription("Provides the MPD status and stats.")
        self.withArgs([
            StringType("status").withLabel("Status").withFeatures({"multiline":True, "maxLines":3}).withProvided(ProvidedMeta().withValue().withReadOnly()),
            StringType("stats").withLabel("Stats").withProvided(ProvidedMeta().withValue().withReadOnly())
        ]).withNoResult().withCallable(False)
        self.withFeatures({"icon":"console", "clearLabel":None, "cancelLabel":"Close", "refreshLabel":None,
                           "refreshEvents":["statusPolling", "mpdNotification"]})
    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        if "status" in context.provide:
            context.provided["status"] =  ProvidedValue().withValue(mpc.getStatus())
        if "stats" in context.provide:
            context.provided["stats"] =  ProvidedValue().withValue(mpc.getStats())

class MpdSetServer(Action):
    def onConfigure(self):
        self.withLabel("Choose an MPD server").withDescription("Sets an MPD server")
        self.withArg(StringType("host").withNullable().withLabel("Host").withDescription("The MPD host").withProvided(
            ProvidedMeta().withValue().withOverwrite()))
        self.withNoResult().withFeatures({"icon":"record-player", "callLabel":"Save"})
    def onCall(self, host):
        if host:
            host = host.strip()
        if host != sponge.getVariable("mpc").host:
            sponge.setVariable("mpc", Mpc(host = host))
    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        if "host" in context.provide:
            context.provided["host"] =  ProvidedValue().withValue(mpc.host)

def onStartup():
    sponge.setVariable("mpc", Mpc())
    sponge.setVariable("lyricsService", LyricsService(sponge.getProperty("musixmatchApiKey", None)))
    sponge.getVariable("mpc").startEventLoop()
    sponge.event("statusPolling").sendEvery(Duration.ofSeconds(1))

def onShutdown():
    sponge.getVariable("mpc").stopEventLoop()
