from __future__ import print_function

from py4j.clientserver import ClientServer, JavaParameters, PythonParameters
import time
import glob
import pathlib
import os
from os.path import basename, splitext
import traceback
import sys
import shutil
import getopt

import mnist_model as model

MNIST_TEMP_DIR = None

# Read the arguments.
opts, args = getopt.getopt(sys.argv[1:], "d:", ["tempdir="])
for opt, arg in opts:
    if opt in ("-d", "--tempdir"):
        MNIST_TEMP_DIR = arg

class MnistService:
    def __init__(self, tmp_dir = None):
        self.tmp_dir = tmp_dir if tmp_dir else "_tmp_"
        self.ready = False
        self.learn_dir = self.tmp_dir + "/learn"
        self.archive_dir = self.tmp_dir + "/archive"
        print("Using temp dir {:s}".format(self.tmp_dir), flush=True)

    def startup(self):
        self.model = model.MnistModel()
        self.model.load()
        self.__relearn__()
        self.ready = True

    def get_learn_files(self):
        return sorted(glob.glob("{:s}/*-*.png".format(self.learn_dir)))

    def __relearn__(self):
        """ Re-learn from files. """
        for file_name in self.get_learn_files():
            with open(file_name, "rb") as f:
                image_data = f.read()
                digit = int(basename(splitext(file_name)[0]).split("-")[1])
                print("Learning digit {:d} from {:s}".format(digit, file_name), flush=True)
                self.model.learn(image_data, digit)

    def isReady(self):
        return self.ready

    def predict(self, image_data):
        predictions = self.model.predict(image_data).tolist()
        result = gateway.jvm.java.util.ArrayList()
        for prediction in predictions:
            result.add(prediction)
        return result

    def learn(self, image_data, digit):
        self.model.learn(image_data, digit)
        self.__write_learned_image_file__(image_data, digit)

    def __write_learned_image_file__(self, image_data, digit):
        """ Write image file. """
        timestamp = int(round(time.time() * 1000.0))
        pathlib.Path(self.learn_dir).mkdir(parents=True, exist_ok=True)
        with open("{:s}/{:s}-{:d}.png".format(self.learn_dir, str(timestamp), digit), "wb") as f:
            f.write(image_data)

    def reset(self):
        self.__archive_learned_image_files__()
        self.model.load()

    def __archive_learned_image_files__(self):
        """ Archive learned files. """
        pathlib.Path(self.archive_dir).mkdir(parents=True, exist_ok=True)
        for file_name in self.get_learn_files():
            shutil.move(file_name, self.archive_dir)

    class Java:
        implements = ["org.openksavi.sponge.tensorflow.mnist.MnistService"]

mnistService = MnistService(MNIST_TEMP_DIR)

PY4J_JAVA_PORT = int(os.getenv("PY4J_JAVA_PORT", -1))
PY4J_PYTHON_PORT = int(os.getenv("PY4J_PYTHON_PORT", -1))
PY4J_AUTH_TOKEN = os.getenv("PY4J_AUTH_TOKEN")

print("Using ports {:d} for Python, {:d} for Java (-1 means the default port).".format(PY4J_JAVA_PORT,
        PY4J_PYTHON_PORT), flush=True)

gateway = ClientServer(python_server_entry_point=mnistService,
    java_parameters=JavaParameters(
        port=PY4J_JAVA_PORT if PY4J_JAVA_PORT > -1 else DEFAULT_PORT, auth_token=PY4J_AUTH_TOKEN),
    python_parameters=PythonParameters(
        port=PY4J_PYTHON_PORT if PY4J_PYTHON_PORT > -1 else DEFAULT_PYTHON_PROXY_PORT, auth_token=PY4J_AUTH_TOKEN))

try:
    mnistService.startup()
except:
    print("MNIST service error:")
    traceback.print_exc(file=sys.stdout)
    raise
finally:
    sys.stdout.flush()
    sys.stderr.flush()

print("MNIST service has started.", flush=True)
