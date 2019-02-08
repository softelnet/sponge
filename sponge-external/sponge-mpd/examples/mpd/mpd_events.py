"""
Sponge Knowledge base
MPD events
"""

from org.openksavi.sponge.mpd.event import MpdEventCategory

class MpdChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdChange")
    def onRun(self, event):
        self.logger.info("[{}] MPD change: {}", event.source.getClass().simpleName, event.source.event)

class MpdErrorListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdError")
    def onRun(self, event):
        self.logger.info("[{}] MPD error: {}", event.source.getClass().simpleName, event.source.message)

class MpdBitrateChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdBitrateChange")
    def onRun(self, event):
        self.logger.info("[{}] Bitrate changed: {} -> {}", event.source.getClass().simpleName, event.source.oldBitrate, event.source.newBitrate)

class MpdConnectionChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdConnectionChange")
    def onRun(self, event):
        self.logger.info("[{}] Connection changed: {}", event.source.getClass().simpleName, event.source.connected)

class MpdOutputChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdOutputChange")
    def onRun(self, event):
        self.logger.info("[{}] Output changed: {}", event.source.getClass().simpleName, event.source.event)

class MpdPlayerChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdPlayerChange")
    def onRun(self, event):
        self.logger.info("[{}] Player changed: {}", event.source.getClass().simpleName, event.source.status)

class MpdVolumeChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdVolumeChange")
    def onRun(self, event):
        self.logger.info("[{}] Volume changed: {}", event.source.getClass().simpleName, event.source.volume)

class MpdPlaylistChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdPlaylistChange")
    def onRun(self, event):
        self.logger.info("[{}] Playlist changed: {}{}", event.source.getClass().simpleName, event.source.event,
                         (", current song: " + mpd.server.playlist.currentSong.file) if event.source.event.name() == "SONG_CHANGED" else "")

class MpdTrackPositionChangeListener(Trigger):
    def onConfigure(self):
        self.withEvent("mpdTrackPositionChange")
    def onRun(self, event):
        self.logger.info("[{}] Track position changed: {}", event.source.getClass().simpleName, event.source.elapsedTime)

def onStartup():
    sponge.logger.info("MPD server version: {}", mpd.server.version)
    #mpd.registerAllListeners()
    mpd.registerListeners(MpdEventCategory.CHANGE, MpdEventCategory.ERROR, MpdEventCategory.PLAYER_CHANGE, MpdEventCategory.PLAYLIST_CHANGE,
                          MpdEventCategory.VOLUME_CHANGE)
