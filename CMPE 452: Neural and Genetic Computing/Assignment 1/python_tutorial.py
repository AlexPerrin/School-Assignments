import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

#A1
# load dataset
df_train = pd.read_csv('data/train.txt', names=['Text', 'Emotion'], sep=';')
df_test = pd.read_csv('data/test.txt', names=['Text', 'Emotion'], sep=';')

column = ["Spongebob", "Patrick", "Squidward"]
titled_column = {"name": column}
titled_columns = {"name": column,
                  "height": [1.67, 1.9, 0.2],
                  "weight": [54, 100, 1]}
data = pd.DataFrame(titled_columns)

select_column = data["weight"]
select_row = data.iloc[1]

bmi = []
# weight/(height**2)
for i in range(len(data)):
    bmi_score = data['weight'][i]/(data['height'][i]**2)
    bmi.append(bmi_score)

#insert manipulated data into a new dataframe column
data['bmi'] = bmi

#save datafram to a file
data.to_csv("bmi.csv")

print(pd.DataFrame(data))
print(pd.DataFrame(select_row))