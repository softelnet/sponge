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

class MpdSetAndPlayPlaylist(Action):
    def onConfigure(self):
        self.label = "Set and play a playlist"
        self.description = "Sets a playlist according to the arguments and starts playing it immediately. Uses mpc"
        self.argsMeta = [
            ArgMeta("artist", StringType().nullable(True)).label("Artist").description("The artist"),
            ArgMeta("album", StringType().nullable(True)).label("Album").description("The album"),
            ArgMeta("genre", StringType().nullable(True)).label("Genre").description("The genre"),
            ArgMeta("minYear", IntegerType().nullable(True)).label("Release year (since)").description("The album minimum release year."),
            ArgMeta("maxYear", IntegerType().nullable(True)).label("Release year (to)").description("The album maximum release year."),
            ArgMeta("autoPlay", BooleanType().defaultValue(True)).label("Auto play").description("Plays the playlist automatically."),
        ]
        self.resultMeta = ResultMeta(StringType()).label("Info").description("A short info of the status of the action call.")
    def onCall(self, artist, album, genre, minYear, maxYear, autoPlay):
        mpc = Mpc()
        sponge.logger.info("Setting the playlist...")
        selectedFiles = mpc.searchFiles(artist, album, genre, minYear, maxYear, useSimpleRegexp = True)
        sponge.logger.info("{} files(s) found".format(len(selectedFiles)))
        if len(selectedFiles) > 0:
            mpc.setAndPlayFiles(selectedFiles, autoPlay)
            return "The playlist is set, {} files(s) found".format(len(selectedFiles))
        else:
            return "No matching files found"
