"""
Sponge Knowledge base
MPD events
"""

from org.openksavi.sponge.mpd.event import MpdEventCategory

class MpdChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdChange"
    def onRun(self, event):
        self.logger.info("[{}] MPD change: {}", event.source.getClass().simpleName, event.source.event)

class MpdErrorListener(Trigger):
    def onConfigure(self):
        self.event = "mpdError"
    def onRun(self, event):
        self.logger.info("[{}] MPD error: {}", event.source.getClass().simpleName, event.source.message)

class MpdBitrateChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdBitrateChange"
    def onRun(self, event):
        self.logger.info("[{}] Bitrate changed: {} -> {}", event.source.getClass().simpleName, event.source.oldBitrate, event.source.newBitrate)

class MpdConnectionChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdConnectionChange"
    def onRun(self, event):
        self.logger.info("[{}] Connection changed: {}", event.source.getClass().simpleName, event.source.connected)

class MpdOutputChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdOutputChange"
    def onRun(self, event):
        self.logger.info("[{}] Output changed: {}", event.source.getClass().simpleName, event.source.event)

class MpdPlayerChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdPlayerChange"
    def onRun(self, event):
        self.logger.info("[{}] Player changed: {}", event.source.getClass().simpleName, event.source.status)

class MpdVolumeChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdVolumeChange"
    def onRun(self, event):
        self.logger.info("[{}] Volume changed: {}", event.source.getClass().simpleName, event.source.volume)

class MpdPlaylistChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdPlaylistChange"
    def onRun(self, event):
        self.logger.info("[{}] Playlist changed: {}{}", event.source.getClass().simpleName, event.source.event,
                         (", current song: " + str(mpd.server.playlist.currentSong)) if event.source.event.name() == "SONG_CHANGED" else "")

class MpdTrackPositionChangeListener(Trigger):
    def onConfigure(self):
        self.event = "mpdTrackPositionChange"
    def onRun(self, event):
        self.logger.info("[{}] Track position changed: {}", event.source.getClass().simpleName, event.source.elapsedTime)

def onStartup():
    EPS.logger.info("MPD server version: {}", mpd.server.version)
    #mpd.registerAllListeners()
    mpd.registerListeners(MpdEventCategory.CHANGE, MpdEventCategory.ERROR, MpdEventCategory.PLAYER_CHANGE, MpdEventCategory.PLAYLIST_CHANGE,
                          MpdEventCategory.VOLUME_CHANGE)
