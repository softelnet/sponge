import mnist_model as m

model = m.MnistModel()
model.load(True)
model.plot_acc()
model.plot_loss()

