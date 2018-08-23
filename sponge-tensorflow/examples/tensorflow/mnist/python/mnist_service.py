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
import signal

import mnist_model as model

class MnistService:
    def __init__(self, model_file, tmp_dir = None):
        self.model_file = model_file
        self.tmp_dir = tmp_dir if tmp_dir else "_tmp_"
        self.ready = False
        self.learn_dir = self.tmp_dir + "/learn"
        self.archive_dir = self.tmp_dir + "/archive"
        print("Using temp dir {:s}".format(self.tmp_dir), flush=True)
        self.gateway = None

    def startup(self, gateway):
        self.gateway = gateway
        self.model = model.MnistModel(self.model_file)
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
        result = self.gateway.jvm.java.util.ArrayList()
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

def main():
    gateway = None
    try:
        mnist_model_file = None
        mnist_temp_dir = None
        
        # Read the arguments.
        opts, args = getopt.getopt(sys.argv[1:], "m:d:", ["modelfile=", "tempdir="])
        for opt, arg in opts:
            if opt in ("-m", "--modelfile"):
                mnist_model_file = arg
            elif opt in ("-d", "--tempdir"):
                mnist_temp_dir = arg
        
        if mnist_model_file is None:
            raise Exception("Model file not provided")
        
        print("Using model file: {}, temporary directory: {}.".format(mnist_model_file,
                mnist_temp_dir), flush=True)
        
        def sigterm_handler(_signo, _stack_frame):
            if gateway:
                gateway.shutdown()
            sys.exit(0)

        signal.signal(signal.SIGTERM, sigterm_handler)
        
        mnistService = MnistService(mnist_model_file, mnist_temp_dir)
        
        py4j_java_port = int(os.getenv("PY4J_JAVA_PORT", -1))
        py4j_python_port = int(os.getenv("PY4J_PYTHON_PORT", -1))
        py4j_auth_token = os.getenv("PY4J_AUTH_TOKEN")
        
        print("Using ports {:d} for Java, {:d} for Python (-1 means the default port).".format(py4j_java_port,
                py4j_python_port), flush=True)
        
        gateway = ClientServer(python_server_entry_point=mnistService,
            java_parameters=JavaParameters(
                port=py4j_java_port if py4j_java_port > -1 else DEFAULT_PORT, auth_token=py4j_auth_token),
            python_parameters=PythonParameters(
                port=py4j_python_port if py4j_python_port > -1 else DEFAULT_PYTHON_PROXY_PORT, auth_token=py4j_auth_token))
    
        mnistService.startup(gateway)
        print("MNIST service has started.", flush=True)
    except:
        print("MNIST service error.")
        traceback.print_exc(file=sys.stdout)
        print("MNIST service has failed to start.", flush=True)
    finally:
        sys.stdout.flush()
        sys.stderr.flush()

if __name__ == '__main__':
    main()