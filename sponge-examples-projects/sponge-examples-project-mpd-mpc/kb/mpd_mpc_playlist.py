"""
Sponge Knowledge base
MPD playlist.
"""

def createPlaylistEntry(name):
    """ Creates a playlist entry record type.
    """
    return RecordType(name).withFields([
        IntegerType("position").withLabel("Position"),
        StringType("song").withLabel("Song")
        ])

class MpdPlaylist(Action):
    def onConfigure(self):
        self.withLabel("Playlist").withDescription("The MPD playlist.")
        self.withArgs([
            ListType("playlist").withLabel("Playlist").withAnnotated().withFeatures(
                {"createAction":"MpdLibrary()", "activateAction":"MpdPlaylistEntryPlay", "pageable":True, "refreshable":True}).withProvided(
                ProvidedMeta().withValue().withOverwrite()).withElement(createPlaylistEntry("song").withAnnotated())
        ]).withCallable(False)
        self.withFeatures({"cancelLabel":"Close", "refreshEvents":["mpdNotification_playlist", "mpdNotification_player"],
                           "contextActions":["MpdLibrary()", "MpdPlaylistClear()"], "icon":"playlist-edit", "visible":True})

    def __createContextActionsForEntry(self, position, entriesSize):
        contextActions = []
        if position is not None and position > 1:
            contextActions.append("MpdPlaylistEntryUp")
        if position is not None and position < entriesSize:
            contextActions.append("MpdPlaylistEntryDown")
        contextActions.append("MpdPlaylistEntryRemove")

        return contextActions

    def onProvideArgs(self, context):
        mpc = sponge.getVariable("mpc")
        status = None

        mpc.lock.lock()
        try:
            if "playlist" in context.provide:
                offset = context.getFeature("playlist", "offset")
                limit = context.getFeature("playlist", "limit")

                allPlaylist = mpc.getPlaylist()
                mpcPlaylist = allPlaylist[offset:(offset + limit)]
                currentPosition = mpc.getCurrentPlaylistPosition()

                entries = list(map(lambda song: AnnotatedValue({"song":song}), mpcPlaylist))
                for pos, entry in enumerate(entries):
                    position = offset + pos + 1
                    entry.value["position"] = position
                    entry.withValueLabel(str(position) + ". " + entry.value["song"])
                    entry.withFeature("contextActions", self.__createContextActionsForEntry(position, len(allPlaylist)))
                    if position == currentPosition:
                        entry.withFeature("icon", "play")

                indicatedIndex = currentPosition - 1 if currentPosition else None
                context.provided["playlist"] = ProvidedValue().withValue(AnnotatedValue(entries).withTypeLabel(
                    "Playlist" + ((" (" + str(len(allPlaylist)) +")") if len(allPlaylist) > 0 else "")).withFeatures(
                        {"offset":offset, "limit":limit, "count":len(allPlaylist), "indicatedIndex":indicatedIndex}))
        finally:
            mpc.lock.unlock()

class MpdPlaylistEntryPlay(Action):
    def onConfigure(self):
        self.withLabel("Play").withArg(createPlaylistEntry("entry").withAnnotated().withFeatures({"visible":False})).withNoResult()
        self.withFeatures({"visible":False, "icon":"play"})
    def onCall(self, entry):
        sponge.getVariable("mpc").playPlaylistEntry(entry.value["position"])

class MpdPlaylistEntryUp(Action):
    def onConfigure(self):
        self.withLabel("Up").withArg(createPlaylistEntry("entry").withAnnotated().withFeatures({"visible":False})).withNoResult()
        self.withFeatures({"visible":False, "icon":"arrow-up-bold"})
    def onCall(self, entry):
        sponge.getVariable("mpc").moveUpPlaylistEntry(entry.value["position"])

class MpdPlaylistEntryDown(Action):
    def onConfigure(self):
        self.withLabel("Down").withArg(createPlaylistEntry("entry").withAnnotated().withFeatures({"visible":False})).withNoResult()
        self.withFeatures({"visible":False, "icon":"arrow-down-bold"})
    def onCall(self, entry):
        sponge.getVariable("mpc").moveDownPlaylistEntry(entry.value["position"])

class MpdPlaylistEntryRemove(Action):
    def onConfigure(self):
        self.withLabel("Remove").withArg(createPlaylistEntry("entry").withAnnotated().withFeatures({"visible":False})).withNoResult()
        self.withFeatures({"visible":False, "icon":"playlist-remove"})
    def onCall(self, entry):
        sponge.getVariable("mpc").removePlaylistEntry(entry.value["position"])

class MpdPlaylistClear(Action):
    def onConfigure(self):
        self.withLabel("Clear playlist").withNoArgs().withNoResult().withFeatures({"visible":False, "icon":"delete-empty"})
    def onCall(self):
        sponge.getVariable("mpc").clearPlaylist()
