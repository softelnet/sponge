"""
Sponge Knowledge base
Py4J Hello world
"""

class PythonUpperCase(Action):
    def onCall(self, text):
        result = py4j.facade.toUpperCase(text)
        self.logger.debug("CPython result for {} is {}", text, result)
        return result
