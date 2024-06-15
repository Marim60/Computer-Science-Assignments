#!/usr/bin/env python
# coding: utf-8

# # Team Members
# | Name            | ID       |
# | :---            | :---     |
# | Somaya Mohammed | 20200234 |
# | Dina Ahmed      | 20201061 |
# | Eman Ibrahim    | 20201038 |
# | Mariem Shehab   | 20200844 |
# | Norhan Sayed    | 20201200 |

# ## 1. Objective:
# • Perform multiple iterations of k (e.g., 5 iterations each different k value ex. K=2,3,4…) on the dataset.
# <br>
# • Use Euclidean distance for computing distances between instances.

# In[ ]:


# #### Load our libraries

# In[1]:


import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split

# #### Load dataset 'diabetes.csv' -> Use the diabetes.csv data to implement your own simple KNN classifier using python

# In[2]:


diabetes = pd.read_csv('dataset/diabetes.csv')

# ## 2. Data preprocessing:
# • Normalize each feature column separately for training and test objects using Log Transformation or Min-Max Scaling.

# In[3]:


# remove null values using built in function
cnt_missing_diabetes = diabetes.isnull().sum()
print(f'Count missing values in "diabetes.csv" dataset: \n{cnt_missing_diabetes}')
print(f'Diabetes dataset shape (m, n): {diabetes.shape}\n')

# print type of
clean_diabetes_type = diabetes.dtypes.map(lambda x: 'categorical' if x == 'object' else 'numerical')
print(f'\ndiabetes type built in function: \n{clean_diabetes_type}\n')

# In[4]:


# the features and targets are separated
features = diabetes.iloc[:, :-1]
target = diabetes.iloc[:, -1]

print(f'features head:\n{features.head()}')
print(f'features shape: {features.shape}\n')
print(f'target head:\n{target.head()}')
print(f'target shape: {target.shape}\n')


# In[5]:


# Min-Max Scaling
def scale(data_features):
    numerical_columns = data_features.select_dtypes(include=['int64', 'float64']).columns
    for column in numerical_columns:
        min_value = data_features[column].min()
        max_value = data_features[column].max()
        scaled_values = [(value - min_value) / (max_value - min_value) for value in data_features[column]]
        data_features[column] = scaled_values
    return data_features


print(f'before scale features:\n{features.head()}\n')
features = scale(features)
print(f'after scale features:\n{features.head()}\n')

# In[6]:


# convert it to numpy to help us in upcomming codes
features = features.to_numpy().reshape((-1, features.shape[1]))
target = target.to_numpy().reshape((-1, 1))

type(features), type(target)

# #### divide your data into 70% for training and 30% for testing. [diabetes.csv]

# In[7]:


x_train, x_test, y_train, y_test = train_test_split(features, target, test_size=0.3, random_state=0)

print(f'x_train shape: {x_train.shape}')
print(f'y_train shape: {y_train.shape}')
print(f'x_test shape: {x_test.shape}')
print(f'y_test shape: {y_test.shape}')


# ## 3. Break ties using Distance-Weighted Voting:
# • When there is a tie, consider the distances between the test instance and the tied classes' neighbors.
# <br>
# • Assign higher weights to closer neighbors and use these weights to break the tie ,
# reflecting the idea that closer neighbors might have a stronger influence on the classification
# decision.
#
# ## 4. Output:
# • For each iteration, output the value of k and the following summary information:
# <br>
#     o Number of correctly classified instances.
#     <br>
#     o Total number of instances in the test set.
#     <br>
#     o Accuracy.
# <br>
# • At the end of all iterations, output the average accuracy across all iterations.

# In[8]:


# Perform multiple iterations of k (e.g., 5 iterations each different k value ex. K=2,3,4…) on the dataset.

def Euclidean_distance(x_train, y_train, x_test):
    m, n = x_train.shape
    # make it as tuple to save target y with his distance with current x_test
    distance = []
    for i in range(m):
        distance.append((np.sqrt(np.sum((x_train[i] - x_test) ** 2)), y_train[i]))
    return distance


def correctly_classified(y_test, y_predicted):
    cnt = 0
    for i in range(len(y_test)):
        if y_test[i] == y_predicted[i]:
            cnt += 1
    return cnt


# as our target is 0 or 1 so we will use odd k to avoid tie case as possible
k = 3
iterations = 5
# variable to output the average accuracy across all iterations
total_accuracy = 0
# we wiil make 5 iterations
for iteration in range(iterations):
    print(f'iteration #{iteration + 1}')
    print(f'current k value = {k}')

    y_pred = np.zeros(y_test.shape[0])
    # brute force over all test cases
    for j in range(x_test.shape[0]):
        distance = Euclidean_distance(x_train, y_train, x_test[j])
        # sort distance to get shortest distance fisrt
        sorted_distance = sorted(distance, key=lambda x: x[0])

        # classify
        class_zero = 0
        for cur in range(k):
            class_zero += 1 if sorted_distance[cur][1] == 0 else 0
        class_one = k - class_zero

        if (class_one == class_zero):
            class_zero = 0
            class_one = 0
            for cur in range(k):
                # print(f'k-cur: {k-cur}')
                if sorted_distance[cur][1] == 0:
                    class_zero += (1 / sorted_distance[cur][0]) * (k - cur)
                else:
                    class_one += (1 / sorted_distance[cur][0]) * (k - cur)

        # predict
        y_pred[j] = 1 if class_one > class_zero else 0

    # for each iteration, output the value of k and the summary information
    correctly_classify = correctly_classified(y_test, y_pred)
    print(f'Number of correctly classified instances: {correctly_classify}')
    print(f'Total number of instances: {y_test.shape[0]}')
    accuracy = (correctly_classify / y_test.shape[0]) * 100
    print(f'Accuracy: {float("{:.4f}".format(accuracy))}%')
    print('---------------------------')
    total_accuracy += accuracy

    # increment k
    k += 1

# At the end of all iterations, output the average accuracy across all iterations.
print(f'average accuracy across {iterations} iterations is {float("{:.4f}".format(total_accuracy / iterations))}%')

# In[ ]:




