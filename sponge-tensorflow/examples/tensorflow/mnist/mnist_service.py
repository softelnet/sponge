from __future__ import print_function

import keras
from keras import layers
from keras import models
from keras import preprocessing
from keras.datasets import mnist
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten, Conv2D, MaxPooling2D
from keras import backend as K

import tensorflow as tf

import numpy as np
from io import BytesIO

import os.path
from py4j.clientserver import ClientServer


class MnistModel:
    def __init__(self):
        self.model = None
        # input image dimensions
        self.img_rows, self.img_cols = 28, 28
        self.model_file = 'mnist_model.h5'
        self.predictionThreshold = 0.75

    def __create_model_and_train(self):
        batch_size = 128
        num_classes = 10
        epochs = 12

        # the data, split between train and test sets
        (x_train, y_train), (x_test, y_test) = mnist.load_data()

        if K.image_data_format() == 'channels_first':
            x_train = x_train.reshape(x_train.shape[0], 1, self.img_rows, self.img_cols)
            x_test = x_test.reshape(x_test.shape[0], 1, self.img_rows, self.img_cols)
            input_shape = (1, img_rows, img_cols)
        else:
            x_train = x_train.reshape(x_train.shape[0], self.img_rows, self.img_cols, 1)
            x_test = x_test.reshape(x_test.shape[0], self.img_rows, self.img_cols, 1)
            input_shape = (self.img_rows, self.img_cols, 1)

        #x_train = x_train[:1000] # Test only
        #y_train = y_train[:1000] # Test only
        x_train = x_train.astype('float32')
        x_test = x_test.astype('float32')
        x_train /= 255.0
        x_test /= 255.0
        print('x_train shape:', x_train.shape)
        print(x_train.shape[0], 'train samples')
        print(x_test.shape[0], 'test samples')

        # convert class vectors to binary class matrices
        y_train = keras.utils.to_categorical(y_train, num_classes)
        y_test = keras.utils.to_categorical(y_test, num_classes)

        model = Sequential()
        model.add(Conv2D(32, kernel_size=(3, 3),
                         activation='relu',
                         input_shape=input_shape))
        model.add(Conv2D(64, (3, 3), activation='relu'))
        model.add(MaxPooling2D(pool_size=(2, 2)))
        model.add(Dropout(0.25))
        model.add(Flatten())
        model.add(Dense(128, activation='relu'))
        model.add(Dropout(0.5))
        model.add(Dense(num_classes, activation='softmax'))

        model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adadelta(),
                      metrics=['accuracy'])

        model.fit(x_train, y_train,
                  batch_size=batch_size,
                  epochs=epochs,
                  verbose=1,
                  validation_data=(x_test, y_test))
        score = model.evaluate(x_test, y_test, verbose=0)

        model.save(self.model_file)

        print('Test loss:', score[0])
        print('Test accuracy:', score[1])

        return model

    def load(self):
        if os.path.exists(self.model_file):
            print('Loading model')
            self.model = models.load_model(self.model_file)
        else:
            print('Creating and training model')
            self.model = self.__create_model_and_train()

        # Hack https://github.com/keras-team/keras/issues/6462
        self.model._make_predict_function()

    def __preprocess_image_to_predict(self, image_data):
        image = preprocessing.image.load_img(BytesIO(image_data), grayscale=True, target_size=(self.img_rows, self.img_cols))
        x = preprocessing.image.img_to_array(image)
        x /= 255.0
        x = np.expand_dims(x, axis=0)
        return (x, image)

    def predict(self, image_data):
        x, image = self.__preprocess_image_to_predict(image_data)

        predictionTensor = self.model.predict(x)[0]
        prediction = np.argmax(predictionTensor)
        predictionProb = np.amax(predictionTensor)

        if predictionProb < self.predictionThreshold:
            print('WARNING: The prediction probability', predictionProb, 'is too low so it could be incorrect!')
        print("Prediction: {}, probability: {:.5f}".format(prediction, np.amax(predictionTensor)), flush=True)

        return predictionTensor

####################################

class MnistService(object):
    def __init__(self):
        self.ready = False
    def startup(self):
        self.model = MnistModel()
        self.model.load()
        self.ready = True
    def isReady(self):
        return self.ready
    def predict(self, image_data):
        predictions = self.model.predict(image_data).tolist()
        result = gateway.jvm.java.util.ArrayList()
        for prediction in predictions:
            result.add(prediction)
        return result
    class Java:
        implements = ["org.openksavi.sponge.tensorflow.MnistService"]

mnistService = MnistService()
gateway = ClientServer(python_server_entry_point=mnistService)
mnistService.startup()

print("MNIST service has started.", flush=True)
