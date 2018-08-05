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

import matplotlib.pyplot as plt

# The model is based on https://github.com/keras-team/keras/blob/master/examples/mnist_cnn.py
# and http://nbviewer.jupyter.org/github/fchollet/deep-learning-with-python-notebooks/blob/master/5.1-introduction-to-convnets.ipynb.
class MnistModel:
    def __init__(self):
        self.model = None
        self.history = None
        # input image dimensions
        self.img_rows, self.img_cols = 28, 28
        self.model_file = '../data/mnist_model.h5'
        self.predictionThreshold = 0.55

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
        model.add(Conv2D(32, (3, 3), activation='relu', input_shape=input_shape))
        model.add(MaxPooling2D((2, 2)))
        model.add(Conv2D(64, (3, 3), activation='relu'))
        model.add(MaxPooling2D((2, 2)))
        model.add(Conv2D(64, (3, 3), activation='relu'))
        model.add(Flatten())
        model.add(Dropout(0.5))
        model.add(Dense(64, activation='relu'))
        model.add(Dense(num_classes, activation='softmax'))

        model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer=keras.optimizers.Adadelta(),
                      metrics=['accuracy'])

        history = model.fit(x_train, y_train,
                  batch_size=batch_size,
                  epochs=epochs,
                  verbose=1,
                  validation_data=(x_test, y_test))
        score = model.evaluate(x_test, y_test, verbose=0)

        model.save(self.model_file)

        model.summary()

        print('Test loss:', score[0])
        print('Test accuracy:', score[1])

        return (model, history)

    def load(self, create_new = False):
        if not create_new and os.path.exists(self.model_file):
            print('Loading model')
            self.model = models.load_model(self.model_file)
        else:
            print('Creating and training model')
            (self.model, self.history) = self.__create_model_and_train()

        # Have to initialize before threading (https://stackoverflow.com/questions/40850089/is-keras-thread-safe)
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

    def plot_loss(self):
        loss = self.history.history['loss']
        val_loss = self.history.history['val_loss']

        epochs = range(1, len(loss) + 1)

        plt.figure()
        # "bo" is for "blue dot"
        plt.plot(epochs, loss, 'bo', label='Training loss')
        # b is for "solid blue line"
        plt.plot(epochs, val_loss, 'b', label='Validation loss')
        plt.title('Training and validation loss')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()

        plt.show(block=False)

    def plot_acc(self):
        acc = self.history.history['acc']
        val_acc = self.history.history['val_acc']

        epochs = range(1, len(acc) + 1)

        plt.figure()
        plt.plot(epochs, acc, 'bo', label='Training acc')
        plt.plot(epochs, val_acc, 'b', label='Validation acc')
        plt.title('Training and validation accuracy')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()

        plt.show(block=False)

