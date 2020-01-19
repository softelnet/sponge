"""
Sponge Knowledge base
MPD library.
"""

import os

class MpdLibrary(Action):
    def onConfigure(self):
        self.withLabel("Library").withDescription("The MPD library.")
        self.withArgs([
            StringType("parentDir").withLabel("Directory").withAnnotated().withDefaultValue(AnnotatedValue("/")).withProvided(
                ProvidedMeta().withValue().withReadOnly().withImplicitMode()),
            # The key is used by the GUI to remember a scroll position in the list for every parentDir.
            ListType("files").withLabel("Files").withAnnotated().withFeatures({"pageable":True, "key":"parentDir"}).withProvided(
                ProvidedMeta().withValue().withOverwrite().withDependency("parentDir").withOptionalMode()).withElement(
                    RecordType("file").withAnnotated().withFields([
                        StringType("file"),
                        BooleanType("isDir")
                    ]).withProvided(ProvidedMeta().withSubmittable(SubmittableMeta().withInfluences(["files", "parentDir"])).withDependency("parentDir")))
        ]).withCallable(False).withFeatures({"icon":"library-music", "contextActions":["MpdRefreshDatabase()"], "visible":True})

    def _isFile(self, file):
        return file is not None and file["file"] != ".." and not file["isDir"]

    def _isDir(self, file):
        return file is not None and file["isDir"]

    def _createFiles(self, parentDir):
        mpc = sponge.getVariable("mpc")

        files = list(map(lambda file: AnnotatedValue({"file":file[0], "isDir":file[1]}).withValueLabel(os.path.basename(file[0])).withFeatures(
            {"contextActions":["MpdLibraryAdd", "MpdLibraryAddAndPlay"]}), mpc.getFiles(parentDir)))
        if parentDir != "/":
            files.insert(0, AnnotatedValue({"file":"..", "isDir":True}).withValueLabel(".."))

        for file in files:
            if file.value["isDir"]:
                file.withFeatures({"activateAction":"submit"})
        return files

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
                    newDir = context.current["files.file"].value
                    parentDir = self._changeDir(parentDir, newDir)
                    files = self._createFiles(parentDir)
                except:
                    self.logger.warn(str(sys.exc_info()[1]))
                    parentDir = context.current["parentDir"].value

            if "files" in context.provide or "files.file" in context.submit:
                if files is None:
                    try:
                        files = self._createFiles(parentDir)
                    except:
                        # Failsafe option.
                        self.logger.warn(str(sys.exc_info()[1]))
                        parentDir = AnnotatedValue("/")
                        files = self._createFiles(parentDir)

                offset = context.getFeature("files", "offset")
                limit = context.getFeature("files", "limit")

                context.provided["files"] = ProvidedValue().withValue(AnnotatedValue(files[offset:(offset + limit)]).withFeatures(
                            {"offset":offset, "limit":limit, "count":len(files)}))
                context.provided["parentDir"] = ProvidedValue().withValue(AnnotatedValue(parentDir))
        finally:
            mpc.lock.unlock()

class MpdLibraryAdd(Action):
    def onConfigure(self):
        self.withLabel("Add to playlist").withArg(RecordType("file").withAnnotated().withFields([
            StringType("file"),
            BooleanType("isDir")]).withFeatures({"visible":False})
        ).withNoResult().withFeatures({"visible":False, "icon":"playlist-plus"})
    def onCall(self, file):
        sponge.getVariable("mpc").addFile(file.value["file"])

class MpdLibraryAddAndPlay(Action):
    def onConfigure(self):
        self.withLabel("Add to playlist and play").withArg(RecordType("file").withAnnotated().withFields([
            StringType("file"),
            BooleanType("isDir")]).withFeatures({"visible":False})
        ).withNoResult().withFeatures({"visible":False, "icon":"playlist-play"})
    def onCall(self, file):
        mpc = sponge.getVariable("mpc")

        mpc.lock.lock()
        try:
            position = len(mpc.getPlaylist())
            mpc.addFile(file.value["file"])
            mpc.play(position=position + 1)
        finally:
            mpc.lock.unlock()
