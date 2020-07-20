"""
Sponge Knowledge Base
"""

# Utility function.
def sendNewsEvent(source, title, delay):
    sponge.event("news").set("source", source).set("title", title).sendAfter(delay)

# Send sample events carrying news on startup.
def onStartup():
    allNews = ["First people landed on Mars!", "Ups", "Martians are happy to meet their neighbors"]
    for i in range(len(allNews)):
        sendNewsEvent("newsSourceA", allNews[i], i * 1000)
