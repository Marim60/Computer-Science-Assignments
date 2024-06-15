import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, MinMaxScaler
from sklearn.metrics import mean_squared_error, r2_score
import random


class NeuralNetwork:
    def __init__(self, x, y, num_hidden_neuron, learn_rate):
        self.x = x
        self.y = y
        self.num_hidden_neuron = num_hidden_neuron
        self.learn_rate = learn_rate
        self.num_output_neuron = np.shape(y)[1]
        self.num_weight_hidden = 1 / np.shape(x)[1]
        self.weight_hidden = np.random.uniform(-self.num_weight_hidden, self.num_weight_hidden,
                                               size=(num_hidden_neuron, np.shape(x)[1]))
        self.num_weight_output = 1 / num_hidden_neuron
        self.weight_output = np.random.uniform(-self.num_weight_output, self.num_weight_output,
                                               size=(self.num_output_neuron, num_hidden_neuron))

    def sigmoid(self, z):
        return 1 / (1 + np.exp(-z))

    def error_output(self, activation_output, y_example):
        error_list = []
        for i in range(len(activation_output)):
            error = (activation_output[i] - y_example[i])
            error_list.append(error)
        return error_list

    def error_hidden(self, activation_hidden, error_output):
        error_list = []
        for i in range(len(activation_hidden)):
            # print(f'weight_output[:,i]{self.weight_output[:,i]}')
            summation = np.dot(error_output, self.weight_output[:, i])
            error = summation * activation_hidden[i] * (1 - activation_hidden[i])
            error_list.append(error)
        return error_list

    def update_weights(self, wight, error, activation=None, x_example=None):
        row = np.shape(wight)[0]
        col = np.shape(wight)[1]
        for i in range(row):
            for j in range(col):
                if activation is not None:
                    wight[i][j] = wight[i][j] - (self.learn_rate * error[i] * activation[i])
                else:
                    wight[i][j] = wight[i][j] - (self.learn_rate * error[i] * x_example[j])
        return wight

    def feed_forward(self, type, x_example, num_of_neuron, weights):
        activation_neurons = []
        for i in range(num_of_neuron):
            f = np.dot(weights[i], x_example)
            if type == 'hidden':
                activation_neurons.append(self.sigmoid(f))
            else:
                activation_neurons.append(f)
        return activation_neurons

    def backpropagation(self, x_example, y_example):
        activation_hidden = self.feed_forward('hidden', x_example, self.num_hidden_neuron, self.weight_hidden)
        activation_output = self.feed_forward('output', activation_hidden, self.num_output_neuron, self.weight_output)
        error_out = self.error_output(activation_output, y_example)
        error_hidden_ = self.error_hidden(activation_hidden, error_out)
        self.weight_output = self.update_weights(self.weight_output, error_out, activation_hidden)
        self.weight_hidden = self.update_weights(self.weight_hidden, error_hidden_, None, x_example)

    # train
    def train(self, epochs, x, y):
        training_examples = np.shape(x)[0]
        for epoch in range(epochs):
            for i in range(training_examples):
                self.backpropagation(x[i], y[i])

    # predict
    def predict(self, X):
        predict_output = []
        training_examples = np.shape(X)[0]
        for i in range(training_examples):
            hidden_output = self.feed_forward('hidden', X[i], self.num_hidden_neuron, self.weight_hidden)
            predict_output.append(
                self.feed_forward('output', hidden_output, self.num_output_neuron, self.weight_output))
        return predict_output

    # mean square error
    def mean_square_error(self, y_true, y_predict):
        return np.mean(np.square(y_true - y_predict))

    def mean_absolute_error(self, y_true, y_pred):
        return np.mean(np.abs(y_true - y_pred))


def split_data(x, y, test_size):
    train_size = 1 - test_size
    split_point = round(train_size * len(x))

    x_train = x[:split_point]
    x_test = x[split_point:]

    y_train = y[:split_point]
    y_test = y[split_point:]

    return x_train, x_test, y_train, y_test

# Load data
concrete_data = pd.read_csv('concrete_data.csv')
concrete_data.dropna(inplace=True)
# Extract feature and target
# Shuffle the data
concrete_data_shuffled = concrete_data.sample(frac=1, random_state=42).reset_index(drop=True)

concrete_data_shuffled.dropna(inplace=True)
# Extract feature and target

x_train = concrete_data_shuffled.iloc[:, : -1]
y_train = concrete_data_shuffled.iloc[:,-1:]
ones_column = np.ones((x_train.shape[0], 1))
x_train = np.c_[ones_column, x_train]

# Split
#x_train, x_test, y_train, y_test = train_test_split(x_train, y_train, test_size=0.25, random_state=0)
x_train, x_test, y_train, y_test = split_data(x_train, y_train, 0.25)

# Normalize
scale = StandardScaler()
x_train = scale.fit_transform(x_train)
x_test = scale.transform(x_test)
y_train = y_train.to_numpy().reshape((-1, 1))
y_test = y_test.to_numpy().reshape((-1, 1))
# Constractor
NN = NeuralNetwork(x=x_train, y=y_train, num_hidden_neuron=10, learn_rate=0.0001)
# Train
NN.train(epochs=100, x=x_train, y=y_train)
# Predict on test
y_predict = NN.predict(x_test)
# Mean Square Error
mse = NN.mean_square_error(y_true=y_test, y_predict=y_predict)
mae = NN.mean_absolute_error(y_test, y_predict)
r2 = r2_score(y_test, y_predict)
print(f'mean square error= {mse}')
print(f'mean absolute error = {mae}')
print(f'r2= {r2}')

# Predict target
# from console
print('Do you want to enter new data record to get the cement strength.?')
choose = int(input('Yes: 1 NO 2: '))
if choose == 1:
    new_data = []
    records = int(input('Enter Number of records: '))
    for i in range(records):
        cement, water, superplasticizer, age = map(float, input(
            "Enter values for cement, water, superficialize, age separated by space: ").split())
        new_data.append([cement, water, superplasticizer, age])
    x_predict = scale.fit_transform(new_data)
    ones_column = np.ones((x_predict.shape[0], 1))
    x_predict = np.c_[ones_column, x_predict]
    target = NN.predict(x_predict)
    print(target)

# from file
concrete_new = pd.read_csv('concrete_new.csv')
x_predict = scale.fit_transform(concrete_new)
ones_column = np.ones((x_predict.shape[0], 1))
x_predict = np.c_[ones_column, x_predict]
target = NN.predict(x_predict)
concrete_new['concrete_compressive_strength'] = target
concrete_new.to_csv('concrete_new.csv', index=False)
print(target)
