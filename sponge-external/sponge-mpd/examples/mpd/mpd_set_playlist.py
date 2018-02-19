"""
Sponge Knowledge base
MPD set a playlist
"""

from collections import defaultdict
import re

def getGenreAlbumDict(albums):
    genres = defaultdict(list)
    for album in albums: 
        genres[album.genre].append(album.artistName + " - " + album.name)
    return genres

def getGenreCountDict(albums):
    genres = defaultdict(int)
    for album in albums: 
        genres[album.genre] += 1
    return genres

def getGenreList(albums):
    result = []
    dict = getGenreCountDict(albums)
    for x in sorted(dict, key = dict.get, reverse = True):
        result.append((x, dict[x]))
    return result

def printAlbumsInfo(albums):
    print "Genre statistic:"
    for (genre, count) in getGenreList(albums):
        print u"\t{}: {}".format(genre, count)

    print "Genre albums:"
    genreAlbumDict = getGenreAlbumDict(albums)
    for genre in genreAlbumDict:
        print u"\t{}: {}".format(genre, genreAlbumDict[genre])

def selectAlbums(albums, yearMin, yearMax, genreRegexp):
    selectedAlbums = []
    for album in albums:
        if album.date and album.date >= str(yearMin) and album.date <= str(yearMax) and album.genre and re.match(genreRegexp.lower(), album.genre.lower()):
            selectedAlbums.append(album)
    return selectedAlbums

def setAndPlayPlaylist(albums):
    if len(albums) == 0:
        return
    mpd.server.playlist.clearPlaylist()
    mpd.server.playlist.insertAlbum(albums[0])
    # Play immediately after inserting the first album
    mpd.server.player.play()
    for album in albums[1:]:
        mpd.server.playlist.insertAlbum(album) 

def onRun():
    albums = mpd.server.musicDatabase.albumDatabase.listAllAlbums()
    EPS.logger.info("MPD server version: {}. All album count: {}", mpd.server.version, len(albums))
    printAlbumsInfo(albums)

    EPS.logger.info("Setting the playlist...")
    # Set the playlist to rock albums released since 1970 
    selectedAlbums = selectAlbums(albums, 1970, 2018, ".*(Rock).*")
    if len(selectedAlbums) > 0:
        setAndPlayPlaylist(selectedAlbums)
        EPS.logger.info("The playlist is set, {} albums found", len(selectedAlbums))
    else:
        EPS.logger.info("No matching albums found")
    return False # Run once mode
