"""
Sponge Knowledge base
MPD library that uses JavaMPD. This file defines MPD specific code that could be used as one of knowledge base files.
"""

from collections import defaultdict
import re


class MpdLibrary:
    def getGenreAlbumDict(self, albums):
        genres = defaultdict(list)
        for album in albums: 
            genres[album.genre].append(album.artistName + " - " + album.name)
        return genres

    def getGenreCountDict(self, albums):
        genres = defaultdict(int)
        for album in albums: 
            genres[album.genre] += 1
        return genres

    def getGenreList(self, albums):
        result = []
        dict = self.getGenreCountDict(albums)
        for x in sorted(dict, key = dict.get, reverse = True):
            result.append((x, dict[x]))
        return result

    def getAlbumsInfo(self, albums):
        info = "Genre statistic:"
        for (genre, count) in self.getGenreList(albums):
            info += u"\n\t{}: {}".format(genre, count)

        info +=  "\nGenre albums:"
        genreAlbumDict = self.getGenreAlbumDict(albums)
        for genre in genreAlbumDict:
            info +=  u"\n\t{}: {}".format(genre, genreAlbumDict[genre])
        return info

    def num(self, name, value, raiseOnError):
        try:
            return int(value) if value else None
        except ValueError:
            if raiseOnError:
                raise Exception("Incorrect value '{}' for {}".format(value, name))
            else:
                return None

    def applySimpleRegexp(self, value, useSimpleRegexp):
        return (".*" + value + ".*") if (useSimpleRegexp and value is not None) else value

    def selectAlbums(self, albums, aArtist, aAlbum, aGenre, aMinYear, aMaxYear, useSimpleRegexp = False):
        selectedAlbums = []
        for album in albums:
            albumDate = self.num("albumDate", album.date, False)
            minYear = self.num("minYear", aMinYear, True)
            maxYear = self.num("maxYear", aMaxYear, True)
            aArtist = self.applySimpleRegexp(aArtist, useSimpleRegexp)
            aAlbum = self.applySimpleRegexp(aAlbum, useSimpleRegexp)
            aGenre = self.applySimpleRegexp(aGenre, useSimpleRegexp)

            if (minYear is None or albumDate and albumDate >= minYear) and (maxYear is None or albumDate and albumDate <= maxYear) \
                and (aArtist is None or album.artistName and re.match(aArtist.lower(), album.artistName.lower())) \
                and (aAlbum is None or album.name and re.match(aAlbum.lower(), album.name.lower())) \
                and (aGenre is None or album.genre and re.match(aGenre.lower(), album.genre.lower())):
                selectedAlbums.append(album)
        return selectedAlbums

    def setAndPlayPlaylist(self, albums, autoPlay):
        if len(albums) == 0:
            return
        mpd.server.playlist.clearPlaylist()
        mpd.server.playlist.insertAlbum(albums[0])
        if autoPlay:
            # Play immediately after inserting the first album
            mpd.server.player.play()
        for album in albums[1:]:
            mpd.server.playlist.insertAlbum(album) 

class MpdSetAndPlayPlaylist(Action):
    def onConfigure(self):
        self.withLabel("Set and play a playlist").withDescription("Sets a playlist according to the arguments and starts playing it immediately.")
        self.withArgs([
            StringType("artist").withNullable().withLabel("Artist").withDescription("Artist may be specified as a regular expression."),
            StringType("album").withNullable().withLabel("Album").withDescription("Album may be specified as a regular expression."),
            StringType("genre").withNullable().withLabel("Genre").withDescription("Genre may be specified as a regular expression."),
            IntegerType("minYear").withNullable().withLabel("Release year (since)").withDescription("An album minimum release year."),
            IntegerType("maxYear").withNullable().withLabel("Release year (to)").withDescription("An album maximum release year."),
            BooleanType("autoPlay").withDefaultValue(True).withLabel("Auto play").withDescription("Plays the playlist automatically.")
        ]).withResult(StringType().withLabel("Info").withDescription("A short info of the status of the action call."))
    def onCall(self, artist, album, genre, minYear, maxYear, autoPlay):
        library = MpdLibrary()

        albums = mpd.server.musicDatabase.albumDatabase.listAllAlbums()
        sponge.logger.info("MPD server version: {}. All album count: {}", mpd.server.version, len(albums))
        sponge.logger.info("{}", library.getAlbumsInfo(albums))

        sponge.logger.info("Setting the playlist...")
        # Set the playlist
        selectedAlbums = library.selectAlbums(albums, artist, album, genre, minYear, maxYear, useSimpleRegexp = True)
        if len(selectedAlbums) > 0:
            library.setAndPlayPlaylist(selectedAlbums, autoPlay)
            return "The playlist is set, {} album(s) found".format(len(selectedAlbums))
        else:
            return "No matching albums found"
