"""
Sponge Knowledge Base
Engine - reload git knowledge base.
"""

from java.util.concurrent.locks import ReentrantLock
from os.path import join
from org.apache.commons.io import FileUtils
from java.io import File

def onInit():
    global RELOAD_LOCK, GIT_SUBDIR, GIT_URL_FILE, GIT_BRANCH_FILE
    RELOAD_LOCK = ReentrantLock(True)

    GIT_SUBDIR = "_local_git_cached"
    GIT_URL_FILE = "_git_url.txt"
    GIT_BRANCH_FILE = "_git_branch.txt"

def getGitKnowledgeBaseDir():
    return join(sponge.getProperty("sponge.workDir"), GIT_SUBDIR)

def removeGitKnowledgeBaseDir():
    gitDir = getGitKnowledgeBaseDir()

    sponge.logger.debug("rm -rf {}".format(gitDir))
    sponge.process("rm", "-rf", gitDir).errorAsException().run()

def getGitUrlFile():
    return join(sponge.getProperty("sponge.workDir"), GIT_URL_FILE)

def getGitBranchFile():
    return join(sponge.getProperty("sponge.workDir"), GIT_BRANCH_FILE)

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

def getSavedGitBranch():
    RELOAD_LOCK.lock()
    try:
        gitBranchFile = File(getGitBranchFile())
        if gitBranchFile.isFile():
            branch = FileUtils.readFileToString(gitBranchFile).strip()
            return branch if len(branch) > 0 else None
        else:
            return None
    finally:
        RELOAD_LOCK.unlock()

def saveGitBranch(branch):
    RELOAD_LOCK.lock()
    try:
        FileUtils.writeStringToFile(File(getGitBranchFile()), branch)
    finally:
        RELOAD_LOCK.unlock()

def reloadEngineWithGit():
    RELOAD_LOCK.lock()

    builder = sponge.process("git", "clone", "-q")
    try:
        gitUrl = getSavedGitUrl()
        if gitUrl is not None:
            removeGitKnowledgeBaseDir()

            branch = getSavedGitBranch()
            if branch:
                builder.arguments("-b", branch)

            builder.arguments(gitUrl, getGitKnowledgeBaseDir()).errorAsString()
            process = builder.build()
            try:
                process.run()
            except:
                raise Exception(process.errorString)
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
            "intent":"reload", "icon":"git"})
        self.withArgs([
            StringType("url").withLabel("Git URL").withDescription("Type the git repository URL.").withNullable().withProvided(
                ProvidedMeta().withValue().withOverwrite()).withFeatures({"multiline":True}),
            StringType("branch").withLabel("Git branch name").withDescription("Type the git branch name.").withNullable().withProvided(
                ProvidedMeta().withValue().withOverwrite())
        ]).withNoResult()
    def onProvideArgs(self, context):
        if "url" in context.provide:
            context.provided["url"] = ProvidedValue().withValue(getSavedGitUrl())
        if "branch" in context.provide:
            context.provided["branch"] = ProvidedValue().withValue(getSavedGitBranch())
    def onCall(self, url, branch):
        RELOAD_LOCK.lock()
        try:
            saveGitUrl(url)
            saveGitBranch(branch)
            if url is None:
                removeGitKnowledgeBaseDir()

            reloadEngineWithGit()
        finally:
            RELOAD_LOCK.unlock()
