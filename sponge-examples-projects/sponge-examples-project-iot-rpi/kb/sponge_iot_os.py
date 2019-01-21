"""
Sponge Knowledge base
OS commands
"""

from org.openksavi.sponge.util.process import ProcessConfiguration

class OsGetDiskSpaceInfo(Action):
    def onConfigure(self):
        self.label = "Get disk space info"
        self.description = "Returns the disk space info."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType().format("console")).label("Disk space info")
    def onCall(self):
        return sponge.process(ProcessConfiguration.builder("df", "-h").outputAsString()).run().outputString

class OsDmesg(Action):
    def onConfigure(self):
        self.label = "Run dmesg"
        self.description = "Returns the dmesg output."
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType().format("console")).label("The dmesg output")
    def onCall(self):
        return sponge.process(ProcessConfiguration.builder("dmesg").outputAsString()).run().outputString
