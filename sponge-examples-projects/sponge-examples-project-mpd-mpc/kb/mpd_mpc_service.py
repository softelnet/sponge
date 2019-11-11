"""
Sponge Knowledge base
MPD/MPC service.
"""

from org.openksavi.sponge.util.process import ProcessConfiguration
from java.util.concurrent.locks import ReentrantLock
import re

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
        sponge.process(self.createProcessBuilder().arguments("add", file)).run().waitFor()

    def addFileAsRecord(self, file):
        self.addFile(file["file"])

    def addFiles(self, files):
        sponge.process(self.createProcessBuilder().arguments("add").inputAsString("\n".join(files))).run().waitFor()

    def addFilesAsRecords(self, files):
        sponge.process(self.createProcessBuilder().arguments("add").inputAsString("\n".join(list(map(lambda file: file["file"], files))))).run().waitFor()

    def setAndPlayFiles(self, files, autoPlay):
        if len(files) == 0:
            return
        self.clearPlaylist()
        self.addFileAsRecord(files[0])
        if autoPlay:
            # Play immediately after inserting the first file
            self.play()
        if len(files) > 1:
            self.addFilesAsRecords(files[1:])

    def playPlaylistEntry(self, position):
        self.__execute("play", str(position))

    def getStatus(self):
        return sponge.process(self.createProcessBuilder().arguments("status").outputAsString()).run().outputString

    def getCurrentSong(self):
        return sponge.process(self.createProcessBuilder().arguments("current").outputAsString()).run().outputString

    def __onMpdEvent(self, event):
        sponge.event("mpdNotification").send()
        sponge.event("mpdNotification_" + event).send()

    def startEventLoop(self):
        self.eventLoopProcess = sponge.process(self.createProcessBuilder().arguments("idleloop").outputAsConsumer
                                               (PyConsumer(lambda line: self.__onMpdEvent(line))).outputLoggingConsumerNone()).run()
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
        lines = status.splitlines()
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

    def isStatusOk(self, status):
        if not status or len(status.strip()) == 0 or self.isStatusNotPlaying(status):
            return False
        return True

    def getStats(self):
        return self.__execute("stats")

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

    def moveUpPlaylistEntry(self, position):
        if position > 1:
            self.__execute("move", str(position), str(position - 1))

    def moveDownPlaylistEntry(self, position):
        self.__execute("move", str(position), str(position + 1))

    def removePlaylistEntry(self, position):
        self.__execute("del", str(position))

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

        print(list(map(createEntry, lines)))
        return list(map(createEntry, lines))

    def clearPlaylist(self):
        self.__execute("clear")

    def refreshDatabase(self):
        self.__execute("update")
