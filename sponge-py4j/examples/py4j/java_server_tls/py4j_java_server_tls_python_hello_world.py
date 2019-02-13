# CPython example

from py4j.java_gateway import JavaGateway, CallbackServerParameters, GatewayParameters
import ssl
import os
import sys
import time

def createTlsJavaGateway():
    # Using PEM file from Py4J examples: https://github.com/bartdag/py4j/blob/master/py4j-python/src/py4j/tests/selfsigned.pem
    pem_file = os.path.join(os.path.split(os.path.split(os.path.realpath(__file__))[0])[:-1][0], "selfsigned.pem")

    client_ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLSv1)
    client_ssl_context.verify_mode = ssl.CERT_REQUIRED
    client_ssl_context.check_hostname = False
    client_ssl_context.load_verify_locations(cafile=pem_file)

    server_ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLSv1)
    server_ssl_context.load_cert_chain(pem_file, password='password')

    callback_server_parameters = CallbackServerParameters(ssl_context=server_ssl_context)
    gateway_parameters = GatewayParameters(ssl_context=client_ssl_context)

    gateway = JavaGateway(gateway_parameters=gateway_parameters,
                callback_server_parameters=callback_server_parameters)
    # It seems SecureServerSocket may need a little more time to initialize on some platforms/slow machines.
    time.sleep(0.500)
    return gateway

gateway = createTlsJavaGateway()

#  The Sponge in other process accessed via Py4J. Note that it doesn't provide a simplified bean property access for getters and setters.
sponge = gateway.entry_point

try:
    print("Connected to {}".format(sponge.getInfo()))
    sponge.event("helloEvent").set("say", "Hello from Python's Py4J").send()
    print("Triggers count: {}, first: {}".format(len(sponge.getEngine().getTriggers()), sponge.getEngine().getTriggers()[0].getMeta().getName()))
finally:
    gateway.shutdown()
