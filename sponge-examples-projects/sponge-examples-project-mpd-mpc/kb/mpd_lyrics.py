"""
Sponge Knowledge base
Uses Musixmatch to get lyrics (requires an API key).
"""

from okhttp3 import HttpUrl, MediaType, RequestBody, Request, OkHttpClient
from com.fasterxml.jackson.databind import ObjectMapper
from java.util import Map

def getLyrics(musixmatchApiKey, artist, title):
    url = HttpUrl.parse("https://api.musixmatch.com/ws/1.1/matcher.lyrics.get").newBuilder().addQueryParameter("format", "json").addQueryParameter(
        "q_artist", artist).addQueryParameter("q_track", title).addQueryParameter("apikey", musixmatchApiKey).build()
    request = Request.Builder().url(url).build()
    response = OkHttpClient().newCall(request).execute()
    responseString = response.body().string()
    sponge.logger.info(responseString)
    responseMap = ObjectMapper().readValue(responseString, Map)
    statusCode = responseMap["message"]["header"]["status_code"]
    if statusCode == 200:
        return responseMap["message"]["body"]["lyrics"]["lyrics_body"]
    elif statusCode == 404:
        return "NOT FOUND"
    else:
        return "Error getting lyrics. Status code: " + str(statusCode)