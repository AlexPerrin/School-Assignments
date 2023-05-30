import numpy as np
import pandas as pd
import matplotlib.pyplot as plt



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



def evaluator(y_test, y_pred):
    ####################################################################################################
    # enter code here to implement the evaluation matrices including confusion matrix, accuracy, precision, recall and f1
    # DO NOT use any python packages such as scikit-learn
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



class SimpleFeedbackLearning(object):
    def __init__(self):
        self.history = {}
        self.history['train_acc'] = []
        self.history['test_acc'] = []
        
    def f(self, x):
        ####################################################################################################
        # 4. enter code here to implement the activation function
        fx = 1 if np.dot(self.W, x) >= self.b else 0

        ####################################################################################################
        return fx
    
    def train(self, x, y, x_test, y_test, learning_rate=0.1, n_iters=10, verbose=True):
        n_train, input_size = x.shape
        n_test = x_test.shape[0]
        # weight initialization
        self.W = np.zeros(input_size)
        self.b = np.zeros(1)

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
                    self.W = self.W + learning_rate * xi
                elif yi == 0 and output == 1:
                    self.W = self.W - learning_rate * xi

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
        y_pred = []
        for xi in x:
            y_pred.append(self.f(xi))

        ####################################################################################################
        return y_pred



####################################################################################################
# enter code here to initialize and train the model
model1 = SimpleFeedbackLearning()
model1.train(x_train, y_train, x_test, y_test)
####################################################################################################



# plot the accuracy
plt.plot(model1.history['train_acc'], label='train_acc')
plt.plot(model1.history['test_acc'], label='test_acc')
plt.legend()
plt.show()



####################################################################################################
# enter code here to evaluate the model with the evaluator function
evaluator(y_test, model1.predict(x_test))

####################################################################################################