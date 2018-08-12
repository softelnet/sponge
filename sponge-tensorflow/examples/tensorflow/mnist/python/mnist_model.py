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

# The model is based on https://github.com/keras-team/keras/blob/master/examples/mnist_cnn.py
# and http://nbviewer.jupyter.org/github/fchollet/deep-learning-with-python-notebooks/blob/master/5.1-introduction-to-convnets.ipynb.
# Test loss: 0.0215
# Test accuracy: 0.9932
# This model is used only for the purpose of showing how to invoke machine learning prediction via a Sponge action.
class MnistModel:
    def __init__(self):
        self.model = None
        self.history = None
        self.batch_size = 128
        self.num_classes = 10
        self.epochs = 12
        # input image dimensions
        self.img_rows, self.img_cols = 28, 28
        self.model_file = '../data/mnist_model.h5'
        self.prediction_threshold = 0.55

    def __load_mnist_data(self):
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

        x_train = x_train.astype('float32')
        x_test = x_test.astype('float32')
        x_train /= 255.0
        x_test /= 255.0
        print('x_train shape:', x_train.shape)
        print(x_train.shape[0], 'train samples')
        print(x_test.shape[0], 'test samples')

        # convert class vectors to binary class matrices
        y_train = keras.utils.to_categorical(y_train, self.num_classes)
        y_test = keras.utils.to_categorical(y_test, self.num_classes)
        
        return (x_train, y_train, x_test, y_test, input_shape)

    def __create_model_and_train(self):
        model = Sequential()
        model.add(Conv2D(32, (3, 3), activation='relu', input_shape=self.input_shape))
        model.add(MaxPooling2D((2, 2)))
        model.add(Conv2D(64, (3, 3), activation='relu'))
        model.add(MaxPooling2D((2, 2)))
        model.add(Conv2D(64, (3, 3), activation='relu'))
        model.add(Flatten())
        model.add(Dropout(0.5))
        model.add(Dense(64, activation='relu'))
        model.add(Dense(self.num_classes, activation='softmax'))

        model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adadelta(),
                      metrics=['accuracy'])

        history = model.fit(self.x_train, self.y_train,
                  batch_size=self.batch_size,
                  epochs=self.epochs,
                  verbose=1,
                  validation_data=(self.x_test, self.y_test))
        model.save(self.model_file)

        self.model = model
        self.history = history

    def evaluate(self, model, x_test, y_test):
        score = model.evaluate(x_test, y_test, verbose=0)
        print('Test loss:', score[0])
        print('Test accuracy:', score[1])
        model.summary()

    def load(self, create_new = False):
        (self.x_train, self.y_train, self.x_test, self.y_test, self.input_shape) = self.__load_mnist_data()

        # Keras hack [https://github.com/tensorflow/tensorflow/issues/14356]
        K.clear_session()

        if not create_new and os.path.exists(self.model_file):
            print('Loading model')
            self.model = models.load_model(self.model_file)
        else:
            print('Creating and training model')
            self.__create_model_and_train()

        # Have to initialize before threading (https://stackoverflow.com/questions/40850089/is-keras-thread-safe)
        self.model._make_predict_function()

        self.evaluate(self.model, self.x_test, self.y_test)

    def __preprocess_image_data(self, image_data):
        image = preprocessing.image.load_img(BytesIO(image_data), grayscale=True, target_size=(self.img_rows, self.img_cols))
        x = preprocessing.image.img_to_array(image)
        x /= 255.0
        x = np.expand_dims(x, axis=0)
        return (x, image)

    def predict(self, image_data):
        x, image = self.__preprocess_image_data(image_data)

        prediction_tensor = self.model.predict(x)[0]
        prediction = np.argmax(prediction_tensor)
        prediction_prob = np.amax(prediction_tensor)

        if prediction_prob < self.prediction_threshold:
            print('WARNING: The prediction probability', prediction_prob, 'is too low so it could be incorrect!')
        print("Prediction: {}, probability: {:.5f}".format(prediction, np.amax(prediction_tensor)), flush=True)

        return prediction_tensor

    def learn(self, image_data, digit):
        x, image = self.__preprocess_image_data(image_data)

        datagen = preprocessing.image.ImageDataGenerator(
            featurewise_center=True,
            featurewise_std_normalization=True,
            rotation_range=20,
            width_shift_range=0.2,
            height_shift_range=0.2,
            horizontal_flip=True)
        datagen.fit(x)
        self.model.fit_generator(datagen.flow(x, keras.utils.to_categorical([digit], self.num_classes), batch_size=1),
                    epochs=self.epochs, verbose=0)
        # Simple version commented
        #self.model.fit(x, keras.utils.to_categorical([digit], self.num_classes), epochs=self.epochs, verbose=0)
