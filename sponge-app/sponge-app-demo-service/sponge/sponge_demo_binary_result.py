"""
Sponge Knowledge base
Demo
"""

class HtmlFileOutput(Action):
    def onConfigure(self):
        self.withLabel("HTML file output").withDescription("Returns the HTML file.")
        self.withNoArgs().withResult(BinaryType().withMimeType("text/html").withLabel("HTML file"))
        self.withFeatures({"icon":"web"})
    def onCall(self):
        return String("""
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
    <head>
      <title>HTML page</title>
    </head>
    <body>
        <!-- Main content -->
        <h1>Header</h1>
        <p>Some text
    </body>
</html>
""").getBytes("UTF-8")

class PdfFileOutput(Action):
    def onConfigure(self):
        self.withLabel("PDF file output").withDescription("Returns the PDF file.")
        self.withNoArgs().withResult(BinaryType().withMimeType("application/pdf").withLabel("PDF file").withFeatures({"icon":"file-pdf"}))
        self.withFeatures({"icon":"file-pdf"})
    def onCall(self):
        return sponge.process(ProcessConfiguration.builder("curl", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                              .outputAsBinary()).run().outputBinary
