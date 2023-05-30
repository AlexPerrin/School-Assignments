import torch
import matplotlib.pyplot as plt
from torchvision.datasets import MNIST

import numpy as np

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
device

def load_MNIST(n_val=10000, n_sample=1000, sample=False):
    n_val = n_val
    n_sample = n_sample
    train = MNIST(root = '.', train = True, download = True)
    test = MNIST(root = '.', train = False, download = True)
    
    # data preprocessing
    x_train, x_test = train.data/255, test.data/255
    x_train = x_train.reshape(x_train.shape[0], -1)
    x_test = x_test.reshape(x_test.shape[0], -1)
    y_train = torch.nn.functional.one_hot(train.targets)
    y_test = torch.nn.functional.one_hot(test.targets)

    data_dict = {}
    if sample:
        data_dict['x_train'] = x_train[:-n_val][:n_sample]
        data_dict['y_train'] = y_train[:-n_val][:n_sample]
        data_dict['x_val'] = x_train[-n_val:][:n_sample//10]
        data_dict['y_val'] = y_train[-n_val:][:n_sample//10]
        data_dict['x_test'] = x_test[:n_sample//10]
        data_dict['y_test'] = y_test[:n_sample//10]
    else:
        data_dict['x_train'] = x_train[:-n_val]
        data_dict['y_train'] = y_train[:-n_val]
        data_dict['x_val'] = x_train[-n_val:]
        data_dict['y_val'] = y_train[-n_val:]
        data_dict['x_test'] = x_test
        data_dict['y_test'] = y_test
    return data_dict

# you can start with a small sample dataset by setting sample=True
data_dict = load_MNIST(sample=False)
print('Train data shape:', data_dict['x_train'].shape)
print('Train labels shape:', data_dict['y_train'].shape)
print('Validation data shape:', data_dict['x_val'].shape)
print('Validation labels shape:', data_dict['y_val'].shape)
print('Test data shape:', data_dict['x_test'].shape)
print('Test labels shape:', data_dict['y_test'].shape)

# plot an example
plt.imshow(data_dict['x_train'][0].reshape(28, 28))
plt.title(data_dict['y_train'][0].argmax().item())
plt.show()

def evaluator(y_test, y_pred):
    ####################################################################################################
    # enter code here to implement the evaluation metrics including confusion matrix, accuracy, precision and recall
    # you can only use Numpy or Pytorch to implement the metrics
    TP = 0
    FP = 0
    TN = 0
    FN = 0
    for y_t,y_p in zip(y_test, y_pred):
        if y_t==1 and y_p==1:
            TP += 1
        elif y_t==0 and y_p==0:
            TN += 1
        elif y_t==1 and y_p==0:
            FN += 1
        elif y_t==0 and y_p==1:
            FP += 1

    confusion_matrix = np.array([[TN,FP], [FN,TP]])
    accuracy = (TP+TN)/(TP+TN+FP+FN)
    percision = TP/(TP+FP)
    recall = TP/(TP+FN)
    f1 = 2*percision*recall/(percision+recall)

    print("Confusion Matrix:")
    for i in confusion_matrix:
        print(i)
    print("Accuracy: %.4f" % accuracy)
    print("Percision: %.4f" % percision)
    print("Recall: %.4f" % recall)
    print("F1: %.4f" % f1)

####################################################################################################

class NN(object):
    def __init__(self, learning_rate, n_iters, batch_size, hidden_size, device, dtype=torch.float32):
        self.learning_rate = learning_rate
        self.n_iters = n_iters
        self.batch_size = batch_size
        self.hidden_size = hidden_size
        self.device = device
        self.dtype = dtype
        self.history = {}
        self.history['train_acc'], self.history['val_acc'], self.history['loss'] = [], [], []
    
    # 5. activation function
    def sigmoid(self, x):
        ####################################################################################################
        # enter code here to implement the activation function
        return 1/(1+torch.exp(-x))
        ####################################################################################################

    def train(self, x, y, x_val, y_val, verbose=1):
        n_train = x.shape[0]
        n_val = x_val.shape[0]
        input_size = x.shape[1]
        num_classes = y.shape[1]
        
        # weight initialization
        self.W1 = torch.randn(input_size, self.hidden_size, dtype=self.dtype, device=self.device) * 0.01
        self.W2 = torch.randn(self.hidden_size, num_classes, dtype=self.dtype, device=self.device) * 0.01

        # TODO: train the weights with the input data and labels
        for i in range(self.n_iters):
            loss = 0
            data = getBatch(x, y, self.batch_size)
            #x_batch, y_batch = getBatch(x, y, self.batch_size)
            for x_batch, y_batch in data:
            #for input in x:
                # 1. forward
                ####################################################################################################
                # enter code here to calculate the hidden layer output and output layer output
                hidden = torch.tensor(torch.zeros(self.hidden_size),device=self.device)
                for hidden_index in range(self.hidden_size):           
                    a = torch.dot(x[i], torch.t(self.W1)[hidden_index])
                    hidden[hidden_index] = self.sigmoid(a)

                output = torch.tensor(torch.zeros(num_classes),device=self.device)
                for output_index in range(num_classes):
                    a = torch.dot(hidden, torch.t(self.W2)[output_index])
                    output[output_index] = self.sigmoid(a)    

                ####################################################################################################

                # 2. error and loss
                ####################################################################################################
                # enter code here to calculate the output error, MSE loss, delta output and delta hidden
                output_error = y_val[i]-output
                loss += torch.sum(torch.square(output_error))
                
                #delta_output = torch.tensor(torch.zeros(num_classes), device=self.device)
                error_term_j = output_error*output*(1-output)
                delta_output = self.learning_rate*error_term_j*output
                
                #delta_hidden = torch.tensor(torch.zeros(self.hidden_size), device=self.device)
                #error_term_h = torch.sum(output_error*output*(1-output)*self.W2)*(1-hidden)
                error_term_h = torch.tensor(torch.zeros(self.hidden_size), device=self.device)
                for i, w_jh in enumerate(self.W2):
                    error_term_h[i] = torch.dot(error_term_j, w_jh)
                delta_hidden = self.learning_rate*error_term_h*hidden
                ####################################################################################################

                # 3. backward
                ####################################################################################################
                # enter code here to calculate delta weights and update the weights
                self.W2 += delta_output
                self.W1 += delta_hidden
                ####################################################################################################

            # calculate the accuracy and save the training history
            y_pred = self.predict(x)
            train_acc = torch.sum(torch.argmax(y, dim=1) == y_pred) / n_train
            self.history['train_acc'].append(train_acc)
            self.history['loss'].append(loss)
            
            y_pred = self.predict(x_val)
            val_acc = torch.sum(torch.argmax(y_val, dim=1) == y_pred) / n_val
            self.history['val_acc'].append(val_acc)
            if verbose:
                print('epoch %d, loss %.4f, train acc %.3f, validation acc %.3f'
                  % (i + 1, loss, train_acc, val_acc))
    
    # 4. predict function 
    def predict(self, x):
        ####################################################################################################
        # enter code here to implement the predict function
        # TODO: use the trained weights to predict labels and return the predicted labels
        # remember to use torch.argmax() to return the true labels
        hidden = torch.tensor(torch.zeros(self.hidden_size),device=self.device)
        output = torch.tensor(torch.zeros(10),device=self.device)
        y_pred = torch.tensor(torch.zeros(x.shape[0],10),device=self.device)
        for i, xi in enumerate(x):
            for hidden_index in range(self.hidden_size):           
                a = torch.dot(xi, torch.t(self.W1)[hidden_index])
                hidden[hidden_index] = self.sigmoid(a)
            for output_index in range(10):
                a = torch.dot(hidden, torch.t(self.W2)[output_index])
                output[output_index] = self.sigmoid(a)
            y_pred[i] = output
        y_pred = torch.argmax(output, dim=1)
        ####################################################################################################
        return y_pred

def getBatch(x, y, batch_size):
    n_epoch = x.shape[0] // batch_size
    for i in range(n_epoch):
        x_batch = x[i * batch_size : (i+1) * batch_size]
        y_batch = y[i * batch_size : (i+1) * batch_size]
        yield x_batch, y_batch
    x_batch = x[(i+1) * batch_size:]
    y_batch = y[(i+1) * batch_size:]    
    yield x_batch, y_batch

####################################################################################################
# enter code here to train Model1
# TODO: set your desired hidden size, learning rate, number of iterations and batch size
# remeber to load the dataset to the device (e.g. data_dict['x_train'].to(device))
model1 = NN(0.1, 10, 1000, 16, device)
data_dict['x_train'] = data_dict['x_train'].to(device)
data_dict['y_train'] = data_dict['y_train'].to(device)
data_dict['x_test'] = data_dict['x_test'].to(device)
data_dict['y_test'] = data_dict['y_test'].to(device)
model1.train(data_dict['x_train'], data_dict['y_train'], data_dict['x_test'], data_dict['y_test'])

####################################################################################################

plt.plot(model1.history['train_acc'], label='train_acc')
plt.plot(model1.history['val_acc'], label='val_acc')
plt.legend()
plt.show()

####################################################################################################
# enter code here to evaluate Model1 with test set
# TODO: use the trained Model1 to predict the labels of test set and evaluate the results with the evaluator
evaluator(data_dict['y_test'], model1.predict(data_dict['x_test']))

####################################################################################################