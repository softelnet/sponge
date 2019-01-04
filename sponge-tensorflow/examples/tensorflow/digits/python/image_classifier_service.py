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
from readerwriterlock import rwlock

class ImageClassifierService:
    def __init__(self, model, model_file, workspace_dir = None):
        self.model = model
        self.model_file = model_file
        self.workspace_dir = workspace_dir if workspace_dir else '_workspace_'
        self.ready = False
        self.learn_dir = self.workspace_dir + '/learn'
        self.archive_dir = self.workspace_dir + '/archive'
        print('Using workspace directory {:s}'.format(self.workspace_dir), flush=True)
        self.gateway = None
        self.lock = rwlock.RWLockFair()

    def startup(self, gateway):
        self.gateway = gateway
        self.model.configure(self.model_file)
        with self.lock.gen_wlock():
            self.model.load()
            self._relearn()
        self.ready = True

    def _get_files_to_learn(self):
        return sorted(glob.glob('{:s}/**/*.png'.format(self.learn_dir)))

    def isReady(self):
        return self.ready

    def getLabels(self):
        result = self.gateway.jvm.java.util.ArrayList()
        for label in self.model.getLabels():
            result.add(label)
        return result

    def predict(self, image_data):
        with self.lock.gen_rlock():
            predictions = self.model.predict(image_data).tolist()
        labels = self.model.getLabels()
        result = self.gateway.jvm.java.util.LinkedHashMap()
        for index, prediction in enumerate(predictions):
            result.put(labels[index], prediction)
        return result

    def addToLearn(self, image_data, label):
        ''' Write image file to learn. '''
        timestamp = int(round(time.time() * 1000.0))
        dir_with_label = self.learn_dir + '/' + label
        with self.lock.gen_wlock():
            pathlib.Path(dir_with_label).mkdir(parents=True, exist_ok=True)
            with open('{:s}/{:s}.png'.format(dir_with_label, str(timestamp)), 'wb') as f:
                f.write(image_data)

    def learn(self, image_data, label):
        print('Learning label {:s}'.format(label), flush=True)
        with self.lock.gen_wlock():
            self.model.learn(image_data, label)
            self.addToLearn(image_data, label)

    def reset(self):
        print('Resetting the model', flush=True)
        with self.lock.gen_wlock():
            self._archive_learned_image_files()
            self.model.load()

    def _relearn(self):
        ''' Re-learn from files. '''
        for file_name in self._get_files_to_learn():
            with open(file_name, 'rb') as f:
                image_data = f.read()
                label = os.path.basename(pathlib.Path(file_name).parent)
                print('Learning label {:s} from {:s}'.format(label, file_name), flush=True)
                self.model.learn(image_data, label)

    def _archive_learned_image_files(self):
        ''' Archive learned files. '''
        pathlib.Path(self.archive_dir).mkdir(parents=True, exist_ok=True)
        for file_name in self._get_files_to_learn():
            label = os.path.basename(pathlib.Path(file_name).parent)
            archive_dir_with_label = self.archive_dir + '/' + label
            pathlib.Path(archive_dir_with_label).mkdir(parents=True, exist_ok=True)
            shutil.move(file_name, archive_dir_with_label)

    class Java:
        implements = ['org.openksavi.sponge.tensorflow.ImageClassifierService']

def run_image_classifier_service(model):
    gateway = None
    try:
        model_file = None
        workspace_dir = None

        # Read the arguments.
        opts, args = getopt.getopt(sys.argv[1:], 'm:w:', ['modelfile=', 'workspacedir='])
        for opt, arg in opts:
            if opt in ('-m', '--modelfile'):
                model_file = arg
            elif opt in ('-w', '--workspacedir'):
                workspace_dir = arg

        if model_file is None:
            raise Exception('Model file not provided')
        
        print('Using model file: {}, workspace directory: {}.'.format(model_file,
                workspace_dir), flush=True)
        
        def sigterm_handler(_signo, _stack_frame):
            if gateway:
                gateway.shutdown()
            sys.exit(0)

        signal.signal(signal.SIGTERM, sigterm_handler)

        service = ImageClassifierService(model, model_file, workspace_dir)

        py4j_java_port = int(os.getenv('PY4J_JAVA_PORT', -1))
        py4j_python_port = int(os.getenv('PY4J_PYTHON_PORT', -1))
        py4j_auth_token = os.getenv('PY4J_AUTH_TOKEN')

        print('Using ports {:d} for Java, {:d} for Python (-1 means the default port).'.format(py4j_java_port,
                py4j_python_port), flush=True)

        gateway = ClientServer(python_server_entry_point=service,
            java_parameters=JavaParameters(
                port=py4j_java_port if py4j_java_port > -1 else DEFAULT_PORT, auth_token=py4j_auth_token),
            python_parameters=PythonParameters(
                port=py4j_python_port if py4j_python_port > -1 else DEFAULT_PYTHON_PROXY_PORT, auth_token=py4j_auth_token))

        service.startup(gateway)
        print('The service has started.', flush=True)
    except:
        print('The service error.')
        traceback.print_exc(file=sys.stdout)
        print('The service has failed to start.', flush=True)
    finally:
        sys.stdout.flush()
        sys.stderr.flush()
