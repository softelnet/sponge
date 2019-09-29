"""
Sponge Knowledge base
Uses mpc (MPD client).
"""

from org.openksavi.sponge.util.process import ProcessConfiguration

class Mpc:
    def __init__(self, mpcExec = "mpc", host = None, port = None):
        self.mpcExec = mpcExec
        self.tags = ["artist", "album", "title", "track", "name", "genre", "date", "composer", "performer", "comment", "disc", "file"]
        self.separator = "\t"
        self.host = host
        self.port = port
        self.eventLoopProcess = None

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

class ViewCurrentSong(Action):
    def onConfigure(self):
        self.withLabel("Current song").withDescription("View the current song.")
        self.withArgs([
            StringType("song").withLabel("Song").withFeatures({"multiline":True, "maxLines":2}).withProvided(
                ProvidedMeta().withValue().withReadOnly()),
            StringType("lyrics").withLabel("Lyrics").withProvided(
                ProvidedMeta().withValue().withReadOnly().withDependency("song")),
        ]).withNoResult().withCallable(False)
        self.withFeatures({"clearLabel":None, "cancelLabel":"Close", "refreshLabel":None, "refreshEvents":["mpdNotification"]})
    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        if "song" in context.names:
            context.provided["song"] = ProvidedValue().withValue(mpc.getCurrentSong())
        if "lyrics" in context.names:
            if context.current["song"] and " - " in context.current["song"]:
                (artist, title) = tuple(context.current["song"].split(" - ", 2))
                musixmatchApiKey = sponge.getProperty("musixmatchApiKey", None)
                if musixmatchApiKey:
                    lyrics = getLyrics(musixmatchApiKey, artist, title)
                    print(lyrics)
                else:
                    lyrics = "*** LYRICS SERVICE NOT CONFIGURED ***"
            else:
                lyrics = ""

            context.provided["lyrics"] = ProvidedValue().withValue(lyrics)

class ViewMpdStatus(Action):
    def onConfigure(self):
        self.withLabel("Player").withDescription("Provides the player status.")
        self.withArgs([
            StringType("status").withLabel("Status").withFeatures({"multiline":True, "maxLines":3}).withProvided(ProvidedMeta().withValue().withReadOnly())
        ]).withNoResult().withCallable(False)
        self.withFeatures({"clearLabel":None, "cancelLabel":"Close", "refreshLabel":None, "refreshEvents":["statusPolling", "mpdNotification"]})
    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        if "status" in context.names:
            context.provided["status"] =  ProvidedValue().withValue(mpc.getStatus())


def onStartup():
    sponge.setVariable("mpc", Mpc())
    sponge.getVariable("mpc").startEventLoop()
    sponge.event("statusPolling").sendEvery(Duration.ofSeconds(1))

def onShutdown():
    sponge.getVariable("mpc").stopEventLoop()
