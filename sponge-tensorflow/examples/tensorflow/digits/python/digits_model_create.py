#
# Run: python3 -i digits_model_create.py
#

import matplotlib.pyplot as plt
import digits_model as m

def plot_acc(history):
    plt.plot(history.history['acc'])
    plt.plot(history.history['val_acc'])
    plt.title('Model accuracy')
    plt.ylabel('Accuracy')
    plt.xlabel('Epoch')
    plt.legend(['Train', 'Test'], loc='upper left')
    plt.show()

def plot_loss(history):
    plt.plot(history.history['loss'])
    plt.plot(history.history['val_loss'])
    plt.title('Model loss')
    plt.ylabel('Loss')
    plt.xlabel('Epoch')
    plt.legend(['Train', 'Test'], loc='upper left')
    plt.show()

if __name__ == '__main__':
    model = m.DigitsModel()
    model.configure('../data/digits_model.h5')
    model.load(True)

    plot_acc(model.history)
    plot_loss(model.history)

