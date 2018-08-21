# CPython example

from py4j.clientserver import ClientServer, JavaParameters, PythonParameters
import os

PY4J_JAVA_PORT = int(os.getenv("PY4J_JAVA_PORT", -1))
PY4J_PYTHON_PORT = int(os.getenv("PY4J_PYTHON_PORT", -1))
PY4J_AUTH_TOKEN = os.getenv("PY4J_AUTH_TOKEN")

class PythonService(object):
    def toUpperCase(self, text):
        print(text)
        return text.upper()
    class Java:
        implements = ["org.openksavi.sponge.py4j.PythonService"]

pythonService = PythonService()

gateway = ClientServer(python_server_entry_point=pythonService,
    java_parameters=JavaParameters(
        port=PY4J_JAVA_PORT if PY4J_JAVA_PORT > -1 else DEFAULT_PORT, auth_token=PY4J_AUTH_TOKEN),
    python_parameters=PythonParameters(
        port=PY4J_PYTHON_PORT if PY4J_PYTHON_PORT > -1 else DEFAULT_PYTHON_PROXY_PORT, auth_token=PY4J_AUTH_TOKEN))

