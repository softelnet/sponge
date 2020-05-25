"""
Sponge Knowledge base
MPD/MPC service.
"""

from java.util.concurrent.locks import ReentrantLock
import re
import os

def createSongType(name):
    """ Creates a song record type.
    """
    return RecordType(name).withFields([
        StringType("artist").withLabel("Artist").withNullable(),
        StringType("album").withLabel("Album").withNullable(),
        StringType("title").withLabel("Title").withNullable(),
        StringType("track").withLabel("Track").withNullable(),
        StringType("genre").withLabel("Genre").withNullable(),
        StringType("date").withLabel("Date").withNullable(),
        StringType("disc").withLabel("Disc").withNullable(),
        StringType("file").withLabel("File")
    ])

class Mpc:
    def __init__(self, mpcExec = "mpc", host = None, port = None):
        self.mpcExec = mpcExec
        self.allTags = ["artist", "album", "title", "track", "name", "genre", "date", "composer", "performer", "comment", "disc", "file"]
        self.tags = ["artist", "album", "title", "track", "genre", "date", "disc", "file"]
        self.separator = "\t"
        self.host = host
        self.port = port
        self.eventLoopProcess = None
        self.lock = ReentrantLock(True)
        self.format = self.separator.join(list(map(lambda tag: "%{}%".format(tag), self.tags)))

    def __createProcess(self):
        args = []
        if self.host:
            args.append("-h")
            args.append(self.host)
        if self.port:
            args.append("-p")
            args.append(str(self.port))

        return sponge.process(self.mpcExec).arguments(args)

    def __execute(self, *argv):
        self.lock.lock()
        try:
            process = self.__createProcess().arguments(argv).outputAsString().errorAsException().run()

            return process.outputString
        finally:
            self.lock.unlock()

    def __num(self, name, value, raiseOnError):
        try:
            return int(value) if value else None
        except ValueError:
            if raiseOnError:
                raise Exception("Incorrect value '{}' for {}".format(value, name))
            else:
                return None

    def validatePrerequisites(self):
        # Check if the mpc is installed.
        self.__createProcess().outputAsString().errorAsString().exceptionOnExitCode(False).run()

    # Data type operations.
    def createSongValue(self, songSpec):
        tagValues = songSpec.split(self.separator)
        if len(tagValues) != len(self.tags):
            return None
        song = {}
        for i in range(len(self.tags)):
            song[self.tags[i]] = tagValues[i].strip() if tagValues[i] else None
        return song

    # Admin operations.

    def getServerVersion(self):
        return self.__execute("version")

    def getStatus(self):
        return self.__execute("status")

    def isStatusOk(self, status):
        if status is None or len(status.strip()) == 0 or self.isStatusNotPlaying(status):
            return False
        return True

    def getStats(self):
        return self.__execute("stats")

    def isConnected(self):
        error = self.__createProcess().outputAsString().errorAsString().exceptionOnExitCode(False).run().errorString
        if error and (error.endswith("Cannot assign requested address") or error.endswith("Connection refused")):
            return False
        return True

    # Playlist operations.

    def addFile(self, file):
        self.__execute("add", file)

    def addFileAsRecord(self, file):
        self.addFile(file["file"])

    def addFiles(self, files):
        self.__createProcess().arguments("add").inputAsString("\n".join(files)).run()

    def addFilesAsRecords(self, files):
        self.__createProcess().arguments("add").inputAsString("\n".join(list(map(lambda file: file["file"], files)))).run()

    def addAndPlayFiles(self, files, autoPlay, replacePlaylist = False):
        if len(files) == 0:
            return
        if replacePlaylist:
            self.clearPlaylist()
        self.addFileAsRecord(files[0])
        if autoPlay:
            # Play immediately after inserting the first file
            self.play()
        if len(files) > 1:
            self.addFilesAsRecords(files[1:])

    def playPlaylistEntry(self, position):
        self.__execute("play", str(position))

    def clearPlaylist(self):
        self.__execute("clear")

    def getPlaylist(self):
        return self.__execute("playlist").splitlines()

    def getCurrentPlaylistPositionAndSize(self, status = None):
        if status is None:
            status = self.getStatus()
        lines = status.splitlines()
        if len(lines) == 3:
            matched = re.match(r"\[.*\]\s*#(\d+)/(\d+)\s*.*", lines[1])
            if matched is not None and len(matched.groups()) == 2:
                (position, size) = matched.groups()
                return (int(position) if position else None, int(size) if size else None)
        return (None, None)

    def getCurrentPlaylistPosition(self, status = None):
        return self.getCurrentPlaylistPositionAndSize(status)[0]

    def getPlaylistSize(self, status = None):
        return self.getCurrentPlaylistPositionAndSize(status)[1]

    def moveUpPlaylistEntry(self, position):
        if position > 1:
            self.__execute("move", str(position), str(position - 1))

    def moveDownPlaylistEntry(self, position):
        self.__execute("move", str(position), str(position + 1))

    def removePlaylistEntry(self, position):
        self.__execute("del", str(position))

    # Library operations.
    def listAllFiles(self):
        return self.__execute("-f", "%file%", "search", "genre", "").splitlines()

    def getFiles(self, parentDir = None):
        """ Returns list of tuples (file, isDir)
        """
        format = self.separator.join(list(map(lambda tag: "%{}%".format(tag), ["file", "title"])))
        lines = sorted((self.__execute("-f", format, "ls", parentDir) if parentDir else self.__execute("ls")).splitlines())

        def createEntry(line):
            elements = line.split("\t")
            if len(elements) == 2:
                return (elements[0], False)
            else:
                return (elements[0], True)

        return list(map(createEntry, lines))

    def refreshDatabase(self):
        self.__execute("update")

    def searchFiles(self, aArtist, aAlbum, aTitle, aGenre, aMinYear, aMaxYear, useSimpleRegexp = False):
        minYear = self.__num("minYear", aMinYear, True)
        maxYear = self.__num("maxYear", aMaxYear, True)
        selectedFiles = []
        fileEntries = self.__execute("-f", self.format, "search", "artist", aArtist if aArtist else "", "album", aAlbum if aAlbum else "",
                                     "title", aTitle if aTitle else "", "genre", aGenre if aGenre else "").splitlines()
        for fileEntry in fileEntries:
            tagValues = fileEntry.split(self.separator)
            file = {}
            for i in range(len(self.tags)):
                file[self.tags[i]] = tagValues[i]
            file["date"] = self.__num("date", file["date"], False) if ("date" in self.tags) else None

            if (minYear is None or file["date"] and file["date"] >= minYear) and (maxYear is None or file["date"] and file["date"] <= maxYear):
                selectedFiles.append(file)

        if "file" in self.tags:
            selectedFiles.sort(key=lambda file: file["file"])

        return selectedFiles

    # Player operations.

    def play(self, position = 1, waitFor = False):
        process = self.__createProcess().arguments("play", str(position)).run()
        if waitFor:
            process.waitFor()

    def getCurrentSong(self):
        return self.createSongValue(self.__execute("current", "-f", self.format))

    def getSongLabel(self, song):
        label = None
        if song:
            if song["artist"] is not None and song["title"] is not None:
                label = u"{} - {}".format(song["artist"], song["title"])
            else:
                label = os.path.basename(song["file"])
        return label

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

    def getPlayingState(self, status):
        lines = status.splitlines()
        if len(lines) == 3:
            matched = re.match(r"\[(.*)\]\s*#\d+/.*", lines[1])
            if matched is not None and len(matched.groups()) == 1:
                return matched.groups()[0]
        return None

    def isStatusPlayingOrPaused(self, status):
        playingState = self.getPlayingState(status)
        return playingState is not None and (playingState == "playing" or playingState == "paused")

    def getVolume(self):
        return self.getVolume(self.getStatus())

    def getVolume(self, status):
        lines = status.splitlines()
        if len(lines) > 0:
            matched = re.match(r"volume:\s*(.+)% .*", lines[-1])
            if matched is not None and len(matched.groups()) == 1:
                volume = matched.groups()[0]
                return int(volume) if volume else None
        return None

    def setVolume(self, volume):
        return self.__execute("volume", str(volume))

    # Events.

    def __onMpdEvent(self, event):
        sponge.event("mpdNotification").send()
        sponge.event("mpdNotification_" + event).send()

    def startEventLoop(self):
        self.eventLoopProcess = self.__createProcess().arguments("idleloop").outputAsConsumer(
                PyConsumer(lambda line: self.__onMpdEvent(line))).outputLoggingConsumerNone().errorAsConsumer().runAsync()
    def stopEventLoop(self):
        if self.eventLoopProcess:
            self.eventLoopProcess.destroy()
            self.eventLoopProcess = None
