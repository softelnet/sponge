"""
Sponge Knowledge base
Uses mpc (MPD client).
"""

from org.openksavi.sponge.util.process import ProcessConfiguration
from java.util.concurrent.locks import ReentrantLock
import re, sys

class Mpc:
    def __init__(self, mpcExec = "mpc", host = None, port = None):
        self.mpcExec = mpcExec
        self.tags = ["artist", "album", "title", "track", "name", "genre", "date", "composer", "performer", "comment", "disc", "file"]
        self.separator = "\t"
        self.host = host
        self.port = port
        self.eventLoopProcess = None
        self.lock = ReentrantLock(True)

    def createProcessBuilder(self):
        args = []
        if self.host:
            args.append("-h")
            args.append(self.host)
        if self.port:
            args.append("-p")
            args.append(str(self.port))

        return ProcessConfiguration.builder(self.mpcExec).arguments(args)

    def listAllFiles(self):
        return sponge.process(self.createProcessBuilder().arguments("-f", "%file%", "search", "genre", "").outputAsString()).run().outputString.splitlines()

    def getServerVersion(self):
        return sponge.process(self.createProcessBuilder().arguments("version").outputAsString()).run().outputString

    def clearPlaylist(self):
        sponge.process(self.createProcessBuilder().arguments("clear")).run().waitFor()

    def num(self, name, value, raiseOnError):
        try:
            return int(value) if value else None
        except ValueError:
            if raiseOnError:
                raise Exception("Incorrect value '{}' for {}".format(value, name))
            else:
                return None

    def searchFiles(self, aArtist, aAlbum, aGenre, aMinYear, aMaxYear, useSimpleRegexp = False):
        minYear = self.num("minYear", aMinYear, True)
        maxYear = self.num("maxYear", aMaxYear, True)
        selectedFiles = []
        format = self.separator.join(list(map(lambda tag: "%{}%".format(tag), self.tags)))
        fileEntries = sponge.process(self.createProcessBuilder().arguments("-f", format, "search",
                    "artist", aArtist if aArtist else "", "album", aAlbum if aAlbum else "", "genre", aGenre if aGenre else "")
                      .outputAsString()).run().outputString.splitlines()
        for fileEntry in fileEntries:
            tagValues = fileEntry.split(self.separator)
            file = {}
            for i in range(len(self.tags)):
                file[self.tags[i]] = tagValues[i]
            file["date"] = self.num("date", file["date"], False) if ("date" in self.tags) else None

            if (minYear is None or file["date"] and file["date"] >= minYear) and (maxYear is None or file["date"] and file["date"] <= maxYear):
                selectedFiles.append(file)

        if ("file" in self.tags):
            selectedFiles.sort(key=lambda file: file["file"])

        return selectedFiles

    def play(self, waitFor = False):
        process = sponge.process(self.createProcessBuilder().arguments("play")).run()
        if waitFor:
            process.waitFor()

    def addFile(self, file):
        sponge.process(self.createProcessBuilder().arguments("add", file["file"])).run().waitFor()

    def addFiles(self, files):
        sponge.process(self.createProcessBuilder().arguments("add").inputAsString("\n".join(list(map(lambda file: file["file"], files))))).run().waitFor()

    def setAndPlayFiles(self, files, autoPlay):
        if len(files) == 0:
            return
        self.clearPlaylist()
        self.addFile(files[0])
        if autoPlay:
            # Play immediately after inserting the first file
            self.play()
        if len(files) > 1:
            self.addFiles(files[1:])

    def getStatus(self):
        return sponge.process(self.createProcessBuilder().arguments("status").outputAsString()).run().outputString

    def getCurrentSong(self):
        return sponge.process(self.createProcessBuilder().arguments("current").outputAsString()).run().outputString

    def startEventLoop(self):
        self.eventLoopProcess = sponge.process(self.createProcessBuilder().arguments("idleloop").outputAsConsumer
                                               (PyConsumer(lambda line: sponge.event("mpdNotification").send())).outputLoggingConsumerNone()).run()
    def stopEventLoop(self):
        if self.eventLoopProcess:
            self.eventLoopProcess.destroy()
            self.eventLoopProcess = None

    def __execute(self, *argv):
        self.lock.lock()
        try:
            process = sponge.process(self.createProcessBuilder().arguments(argv).outputAsString().errorAsException()).run()
            #process = sponge.process(self.createProcessBuilder().arguments(argv).outputAsString().errorAsString().exceptionOnExitCode(False)).run()
            #if process.errorString:
            #     sponge.logger.warn(process.errorString)

            return process.outputString
        finally:
            self.lock.unlock()

    def seekByPercentage(self, value):
        return self.__execute("seek", str(value) + "%")

    # Returns a 3-element tuple.
    def getPositionTuple(self, status):
        lines = status.splitlines();
        if len(lines) == 3:
            matched = re.match(r".+ (.*)/(.*) \((.*)%\)", lines[1])
            if matched is not None and len(matched.groups()) == 3:
                return matched.groups()
        return None

    def getPositionByPercentage(self):
        return self.getPositionByPercentage(self.getStatus())

    def getPositionByPercentage(self, status):
        position = self.getPositionTuple(status)
        return int(position[2]) if position else None

    def getTimeStatus(self, status):
        position = self.getPositionTuple(status)
        return position[0] + "/" + position[1] if position else None

    def getVolume(self):
        return self.getVolume(self.getStatus())

    def getVolume(self, status):
        lines = status.splitlines();
        if len(lines) > 0:
            matched = re.match(r"volume:\s*(.+)% .*", lines[-1])
            if matched is not None and len(matched.groups()) == 1:
                volume = matched.groups()[0]
                return int(volume) if volume else None
        return None

    def setVolume(self, volume):
        return self.__execute("volume", str(volume))

    def getPlay(self, status):
        lines = status.splitlines();
        return len(lines) == 3 and re.match(r"\[playing\] .*", lines[1]) is not None

    def togglePlay(self, play):
        return self.__execute("play" if play else "pause")

    def prev(self):
        status = self.__execute("prev")
        if self.isStatusNotPlaying(status):
            return None
        return status

    def next(self):
        status = self.__execute("next")
        if self.isStatusNotPlaying(status):
            return None
        return status

    def isStatusNotPlaying(self, status):
        lines = status.splitlines()
        return len(lines) == 1 and re.match(r".* Not playing .*", lines[0]) is not None

    def isStatusOk(self, status):
        if not status or len(status.strip()) == 0 or self.isStatusNotPlaying(status):
            return False
        return True

    def getStats(self):
        return self.__execute("stats")

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
                    (artist, title) = tuple(context.current["song"].split(" - ", 2))
                    musixmatchApiKey = sponge.getProperty("musixmatchApiKey", None)
                    if musixmatchApiKey:
                        lyrics = getLyrics(musixmatchApiKey, artist, title)
                    else:
                        lyrics = "LYRICS SERVICE NOT CONFIGURED"
                else:
                    lyrics = ""
            except:
                lyrics = "LYRICS ERROR: " + str(sys.exc_info()[0])

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

class MpdPlayer(Action):
    def onConfigure(self):
        self.withLabel("Player").withDescription("The MPD player.")
        self.withArgs([
            StringType("song").withLabel("Song").withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue().withReadOnly()),
            IntegerType("position").withLabel("Position").withMinValue(0).withMaxValue(100).withFeatures({"widget":"slider"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            StringType("time").withLabel("Time").withNullable().withProvided(
                ProvidedMeta().withValue().withReadOnly()),#.withDependency("position").withLazyUpdate()
            IntegerType("volume").withLabel("Volume (%)").withAnnotated().withMinValue(0).withMaxValue(100).withFeatures({"widget":"slider"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()),
            VoidType("prev").withLabel("Previous").withProvided(ProvidedMeta().withSubmittable()),
            BooleanType("play").withLabel("Play").withProvided(
                ProvidedMeta().withValue().withOverwrite().withSubmittable()).withFeatures({"widget":"switch"}),
            VoidType("next").withLabel("Next").withProvided(ProvidedMeta().withSubmittable())
        ]).withNoResult().withCallable(False)
        self.withFeatures({"clearLabel":None, "cancelLabel":"Close", "refreshLabel":None, "refreshEvents":["statusPolling", "mpdNotification"],
                           "icon":"music"})
        self.withFeature("contextActions", [
            "MpdSetAndPlayPlaylist()", "ViewSongLyrics()", "ViewMpdStatus()",
        ])

    def __ensureStatus(self, mpc, status):
        return status if mpc.isStatusOk(status) else mpc.getStatus()

    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        status = None

        mpc.lock.lock()
        try:
            if "position" in context.submit:
                status = mpc.seekByPercentage(context.current["position"])
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
                context.provided["position"] = ProvidedValue().withValue(mpc.getPositionByPercentage(status))
            if "time" in context.provide:
                status = self.__ensureStatus(mpc, status)
                context.provided["time"] = ProvidedValue().withValue(mpc.getTimeStatus(status))
            # Provide an annotated volume value at once if submitted.
            if "volume" in context.provide or "volume" in context.submit:
                status = self.__ensureStatus(mpc, status)
                volume = mpc.getVolume(status)
                context.provided["volume"] = ProvidedValue().withValue(AnnotatedValue(volume).withLabel("Volume (" + str(volume) + "%)"))
            if "play" in context.provide:
                status = self.__ensureStatus(mpc, status)
                context.provided["play"] = ProvidedValue().withValue(mpc.getPlay(status))
        finally:
            mpc.lock.unlock()

def onStartup():
    sponge.setVariable("mpc", Mpc())
    sponge.getVariable("mpc").startEventLoop()
    sponge.event("statusPolling").sendEvery(Duration.ofSeconds(1))

def onShutdown():
    sponge.getVariable("mpc").stopEventLoop()
