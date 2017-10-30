# CPython example

from py4j.clientserver import ClientServer

class PythonService(object):
    def toUpperCase(self, text):
        print(text)
        return text.upper()
    class Java:
        implements = ["org.openksavi.sponge.py4j.PythonService"]

pythonService = PythonService()
gateway = ClientServer(python_server_entry_point=pythonService)

