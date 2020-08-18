"""
Sponge Knowledge Base
Engine - reload git knowledge base.
"""

from java.util.concurrent.locks import ReentrantLock
from os.path import join
from org.apache.commons.io import FileUtils
from java.io import File

def onInit():
    global RELOAD_LOCK, GIT_SUBDIR, GIT_URL_FILE
    RELOAD_LOCK = ReentrantLock(True)

    GIT_SUBDIR = "_local_git_cached"
    GIT_URL_FILE = "_git_url.txt"

def getGitKnowledgeBaseDir():
    return join(sponge.getProperty("sponge.workDir"), GIT_SUBDIR)

def removeGitKnowledgeBaseDir():
    gitDir = getGitKnowledgeBaseDir()

    sponge.logger.debug("rm -rf {}".format(gitDir))
    sponge.process("rm", "-rf", gitDir).errorAsException().run()

def getGitUrlFile():
    return join(sponge.getProperty("sponge.workDir"), GIT_URL_FILE)

def getSavedGitUrl():
    RELOAD_LOCK.lock()
    try:
        gitUrlFile = File(getGitUrlFile())
        if gitUrlFile.isFile():
            url = FileUtils.readFileToString(gitUrlFile).strip()
            return url if len(url) > 0 else None
        else:
            return None
    finally:
        RELOAD_LOCK.unlock()

def saveGitUrl(url):
    RELOAD_LOCK.lock()
    try:
        FileUtils.writeStringToFile(File(getGitUrlFile()), url) 
    finally:
        RELOAD_LOCK.unlock()

def reloadEngineWithGit():
    RELOAD_LOCK.lock()
    try:
        gitUrl = getSavedGitUrl()
        if gitUrl is not None:
            removeGitKnowledgeBaseDir()

            sponge.process("git", "clone", gitUrl, getGitKnowledgeBaseDir()).run()
        sponge.reload()
    finally:
        RELOAD_LOCK.unlock()

class EngineReload(Action):
    def onConfigure(self):
        self.withLabel("Reload Sponge knowledge bases").withDescription("Reloads Sponge knowledge bases.").withFeatures({
            "intent":"reload", "confirmation":True})
        self.withNoArgs().withNoResult()
        self.withFeature("icon", "server")
    def onCall(self):
        reloadEngineWithGit()

class SetupGitKnowledgeBase(Action):
    def onConfigure(self):
        self.withLabel("Setup git knowledge base").withDescription("Sets up the git knowledge base.").withFeatures({
            "intent":"reload"})
        self.withArg(StringType("url").withLabel("Git URL").withDescription("Type the git repository URL.").withNullable(True).withProvided(
            ProvidedMeta().withValue().withOverwrite()).withFeatures({"multiline":True}))
        self.withNoResult()
        self.withFeature("icon", "git")
    def onProvideArgs(self, context):
        if "url" in context.provide:
            context.provided["url"] = ProvidedValue().withValue(getSavedGitUrl())
    def onCall(self, url):
        RELOAD_LOCK.lock()
        try:
            saveGitUrl(url)
            if url is None:
                removeGitKnowledgeBaseDir()

            reloadEngineWithGit()
        finally:
            RELOAD_LOCK.unlock()
