import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import random



# load dataset
df_train = pd.read_csv('data/train.txt', names=['Text', 'Emotion'], sep=';')
df_test = pd.read_csv('data/test.txt', names=['Text', 'Emotion'], sep=';')



x_train = df_train['Text']
y_train = df_train['Emotion']

x_test = df_test['Text']
y_test = df_test['Emotion']

df_train.head()



df_train.Emotion.value_counts()



# encode label
y_train = y_train.replace({'joy':1, 'sadness':0, 'anger':0, 'fear':0, 'love':1, 'surprise':1})
y_test = y_test.replace({'joy':1, 'sadness':0, 'anger':0, 'fear':0, 'love':1, 'surprise':1})



from sklearn.feature_extraction.text import TfidfVectorizer
tfidf = TfidfVectorizer(sublinear_tf=True, min_df=5)#, stop_words='english')

# We transform each text into a vector
x_train = tfidf.fit_transform(x_train).toarray()
x_test = tfidf.transform(x_test).toarray()



#print(len(x_train))
#print(len(x_train[0]))

#print(len(x_test))
#print(len(x_test[0]))

#for i in range(len(np.nonzero(x_train)[0])):
#    print('(',np.nonzero(x_train)[0][i],', ',np.nonzero(x_train)[1][i],') i=(',)



def evaluator(y_test, y_pred):
    ####################################################################################################
    # enter code here to implement the evaluation matrices including confusion matrix, accuracy, precision, recall and f1
    # DO NOT use any python packages such as scikit-learn
    NOP

    ####################################################################################################


class SimpleFeedbackLearning(object):
    def __init__(self):
        self.history = {}
        self.history['train_acc'] = []
        self.history['test_acc'] = []
   
    def f(self, x):
        ####################################################################################################
        # 4. enter code here to implement the activation function
        #fx = 1 if np.dot(self.W, x) >= self.b else 0
        fx = 0
        print(np.dot(self.W, x))
        ####################################################################################################
        return fx
    
    def train(self, x, y, x_test, y_test, learning_rate=0.1, n_iters=10, verbose=True):
        n_train, input_size = x.shape
        n_test = x_test.shape[0]
        # weight initialization
        #self.W = np.zeros(input_size)
        #self.b = np.zeros(1)

        self.W = np.ones(input_size)
        self.b = 0

        for i in range(n_iters):
            for xi, yi in zip(x, y):
                # forward
                ####################################################################################################
                # 1. enter code here to calculate the output
                output = self.f(xi)
                ####################################################################################################

                ####################################################################################################
                # 2. enter code here to adjust the weights and bias
                if yi == 1 and output == 0:
                    self.W = self.W + learning_rate*x
                    self.b = self.b - learning_rate
                elif yi == 0 and output == 1:
                    self.W = self.W - learning_rate*x
                    self.b = self.b + learning_rate
                
                ####################################################################################################
            

            train_acc = (self.predict(x) == y).sum() / n_train
            test_acc = (self.predict(x_test) == y_test).sum() / n_test
            self.history['train_acc'].append(train_acc)
            self.history['test_acc'].append(test_acc)
            if verbose:
                print('epoch %d, train acc %.4f, test acc %.4f' % (i + 1, train_acc, test_acc))

    
    def predict(self, x):
        ####################################################################################################
        # 3. enter code here to complete the predict function
        # TODO: use the trained weights to predict labels and return the predicted labels
        y_pred = np.array(x.shape)
        for xi in x:
            np.append(y_pred, self.f(xi))

        ####################################################################################################
        return y_pred


####################################################################################################
# enter code here to initialize and train the model
model1 = SimpleFeedbackLearning()
model1.train(x_train, y_train, x_test, y_test)

####################################################################################################



## plot the accuracy
#plt.plot(model1.history['train_acc'], label='train_acc')
#plt.plot(model1.history['test_acc'], label='test_acc')
#plt.legend()
#plt.show()



####################################################################################################
# enter code here to evaluate the model with the evaluator function


####################################################################################################



