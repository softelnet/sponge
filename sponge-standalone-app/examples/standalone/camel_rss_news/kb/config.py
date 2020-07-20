"""
Sponge Knowledge Base
"""

# Set configuration variables.
# For the sake of clarity setting of configuration variables is done in the main level of the script. This code typically would be
# in onInit() callback function. However, because these are constants, a potential reload (causing this code to be executed once more)
# wouldn't cause any problems.
sponge.setVariable("rssSources", {"BBC":"http://rss.cnn.com/rss/edition.rss", "CNN":"http://feeds.bbci.co.uk/news/world/rss.xml"})
