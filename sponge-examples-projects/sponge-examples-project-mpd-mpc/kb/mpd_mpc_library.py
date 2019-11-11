"""
Sponge Knowledge base
MPD library.
"""

import os

class MpdLibrary(Action):
    def onConfigure(self):
        self.withLabel("Library").withDescription("The MPD library.")
        self.withArgs([
            StringType("parentDir").withLabel("Directory").withAnnotated().withDefaultValue("/").withProvided(
                ProvidedMeta().withValue().withReadOnly().withOptional()),
            ListType("files").withLabel("Files").withAnnotated().withFeatures({"scroll":True}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("parentDir")).withElement(
                    RecordType("file").withAnnotated().withFields([
                        StringType("file"),
                        BooleanType("isDir")
                    ]).withProvided(ProvidedMeta().withSubmittable().withDependency("parentDir")))
        ]).withNoResult().withCallable(False)
        self.withFeatures({"clearLabel":None, "cancelLabel":None, "refreshLabel":None, "icon":"library-music", "contextActions":["MpdRefreshDatabase()"]})

    def _isFile(self, file):
        return file is not None and file["file"] != ".." and not file["isDir"]

    def _isDir(self, file):
        return file is not None and file["isDir"]

    def _createFilesArg(self, parentDir):
        mpc = sponge.getVariable("mpc")

        files = list(map(lambda file: AnnotatedValue({"file":file[0], "isDir":file[1]}).withValueLabel(os.path.basename(file[0])).withFeatures(
            {"contextActions":["MpdLibraryAdd", "MpdLibraryAddAndPlay"]}), mpc.getFiles(parentDir)))
        if parentDir != "/":
            files.insert(0, AnnotatedValue({"file":"..", "isDir":True}).withValueLabel(".."))

        for file in files:
            if file.value["isDir"]:
                file.withFeatures({"activateAction":"submit"})
        return AnnotatedValue(files)

    def _changeDir(self, parentDir, file):
        if not self._isDir(file):
            return parentDir
        # Go up.
        if file["file"] == "..":
            if parentDir == "/":
                return parentDir
            else:
                newDir = os.path.split(parentDir)[0]
                return newDir if len(newDir) > 0 else "/"
        else:
            return file["file"]

    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")

        mpc.lock.lock()
        try:
            files = None
            parentDir = context.current["parentDir"].value
            if "files.file" in context.submit:
                try:
                    parentDir = self._changeDir(parentDir, context.current["files.file"].value)
                    files = self._createFilesArg(parentDir)
                except:
                    self.logger.warn(str(sys.exc_info()[1]))
                    parentDir = context.current["parentDir"].value

            if "files" in context.provide or "files.file" in context.submit:
                if files is None:
                    files = self._createFilesArg(parentDir)

                context.provided["files"] = ProvidedValue().withValue(files)
                context.provided["parentDir"] = ProvidedValue().withValue(parentDir)
        finally:
            mpc.lock.unlock()

class MpdLibraryAdd(Action):
    def onConfigure(self):
        self.withLabel("Add to playlist").withArg(RecordType("file").withAnnotated().withFields([
            StringType("file"),
            BooleanType("isDir")])
        ).withNoResult().withFeatures({"visible":False, "icon":"playlist-plus"})
    def onCall(self, file):
        sponge.getVariable("mpc").addFile(file.value["file"])

class MpdLibraryAddAndPlay(Action):
    def onConfigure(self):
        self.withLabel("Add to playlist and play").withArg(RecordType("file").withAnnotated().withFields([
            StringType("file"),
            BooleanType("isDir")])
        ).withNoResult().withFeatures({"visible":False, "icon":"playlist-play"})
    def onCall(self, file):
        mpc = sponge.getVariable("mpc")

        mpc.lock.lock()
        try:
            mpc.addFile(file.value["file"])
            mpc.play(position=mpc.getPlaylistSize())
        finally:
            mpc.lock.unlock()
