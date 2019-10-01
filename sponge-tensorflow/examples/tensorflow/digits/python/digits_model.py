from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers, models, datasets, preprocessing
from tensorflow.keras import backend as K
import numpy as np
from io import BytesIO
import os.path

print('TensorFlow version', tf.__version__)
print('Keras version', keras.__version__)

# The model is based on https://github.com/keras-team/keras/blob/master/examples/mnist_cnn.py
# and http://nbviewer.jupyter.org/github/fchollet/deep-learning-with-python-notebooks/blob/master/5.1-introduction-to-convnets.ipynb.
# Test loss: 0.0197
# Test accuracy: 0.9936
# This model is used only for the purpose of showing how to invoke machine learning prediction via a Sponge action.
class DigitsModel:
    def __init__(self):
        self.model = None
        self.history = None
        self.batch_size = 128
        self.num_classes = 10
        self.epochs = 12
        self.manualLearnEpochs = 6
        # Input image dimensions.
        self.img_rows, self.img_cols = 28, 28
        self.labels = list(map(str, range(10)))

    def configure(self, model_file):
        self.model_file = model_file

    def _load_mnist_data(self):
        # The data, split between train and test sets.
        (x_train, y_train), (x_test, y_test) = datasets.mnist.load_data()

        if K.image_data_format() == 'channels_first':
            x_train = x_train.reshape(x_train.shape[0], 1, self.img_rows, self.img_cols)
            x_test = x_test.reshape(x_test.shape[0], 1, self.img_rows, self.img_cols)
            input_shape = (1, self.img_rows, self.img_cols)
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

        # Convert class vectors to binary class matrices.
        y_train = keras.utils.to_categorical(y_train, self.num_classes)
        y_test = keras.utils.to_categorical(y_test, self.num_classes)

        return (x_train, y_train, x_test, y_test, input_shape)

    def _create_model_and_train(self):
        model = models.Sequential()
        model.add(layers.Conv2D(32, (3, 3), activation='relu', input_shape=self.input_shape))
        model.add(layers.MaxPooling2D((2, 2)))
        model.add(layers.Conv2D(64, (3, 3), activation='relu'))
        model.add(layers.MaxPooling2D((2, 2)))
        model.add(layers.Conv2D(64, (3, 3), activation='relu'))
        model.add(layers.Flatten())
        model.add(layers.Dropout(0.5))
        model.add(layers.Dense(64, activation='relu'))
        model.add(layers.Dense(self.num_classes, activation='softmax'))

        model.compile(loss=keras.losses.categorical_crossentropy,
                      optimizer='adam',
                      metrics=['acc'])

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
        (self.x_train, self.y_train, self.x_test, self.y_test, self.input_shape) = self._load_mnist_data()

        if not create_new and os.path.exists(self.model_file):
            print('Loading model')
            self.model = models.load_model(self.model_file)
        else:
            print('Creating and training a new model')
            self._create_model_and_train()

        self.evaluate(self.model, self.x_test, self.y_test)

    def _preprocess_image_data(self, image_data):
        image = preprocessing.image.load_img(BytesIO(image_data), color_mode = 'grayscale', target_size=(self.img_rows, self.img_cols))
        image_tensor = preprocessing.image.img_to_array(image)
        image_tensor /= 255.0
        image_tensor = np.expand_dims(image_tensor, axis=0)
        return (image_tensor, image)

    def getLabels(self):
        return self.labels

    def predict(self, image_data):
        image_tensor, image = self._preprocess_image_data(image_data)
        prediction_tensor = self.model.predict(image_tensor)[0]

        return prediction_tensor

    def learn(self, image_data, digit):
        x, image = self._preprocess_image_data(image_data)
        self.model.fit(x,
                       keras.utils.to_categorical([digit], self.num_classes),
                       epochs=self.manualLearnEpochs,
                       verbose=0,
                       validation_data=(self.x_test, self.y_test))
