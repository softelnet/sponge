"""
Sponge Knowledge base
OS commands
"""

class OsGetDiskSpaceInfo(Action):
    def onConfigure(self):
        self.withLabel("Get disk space info").withDescription("Returns the disk space info.")
        self.withNoArgs().withResult(StringType().withFormat("console").withLabel("Disk space info"))
        self.withFeature("icon", "console")
    def onCall(self):
        return sponge.process("df", "-h").outputAsString().run().outputString

class OsDmesg(Action):
    def onConfigure(self):
        self.withLabel("Run dmesg").withDescription("Returns the dmesg output.")
        self.withNoArgs().withResult(StringType().withFormat("console").withLabel("The dmesg output"))
        self.withFeature("icon", "console")
    def onCall(self):
        return sponge.process("dmesg").outputAsString().run().outputString
