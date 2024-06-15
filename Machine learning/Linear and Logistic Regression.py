#!/usr/bin/env python
# coding: utf-8


# In[1]:


import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.preprocessing import LabelEncoder
from sklearn.utils import shuffle


# # Data Preparation
# ### a) h) Load the "loan_old.csv" & "loan_new.csv" dataset.

# In[2]:


# Load the "loan_old.csv" dataset.
loan_old = pd.read_csv('loan_old.csv')
# Load the "loan_new.csv" dataset.
loan_new = pd.read_csv('loan_new.csv')

print(f" loan_old shape: {loan_old.shape}")
print(f" loan_new shape: {loan_new.shape}")


# ### b) Perform analysis on the dataset.

# In[3]:


# i) check whether there are missing values
missing_values_old = loan_old.isnull().sum()
print(f"i) missing values old:\n {missing_values_old}")
print()
missing_values_new = loan_new.isnull().sum()
print(f"i) missing values new:\n {missing_values_new}")


# In[4]:


# ii) check the type of each feature (categorical or numerical)
feature_types_old = loan_old.dtypes.map(lambda x: 'categorical' if x == 'object' else 'numerical')
print(f"ii) type of each feature in loan old: \n{feature_types_old}")


# In[5]:


# iii) check whether numerical features have the same scale
numerical_features = loan_old.select_dtypes(include=['int64', 'float64'])
print(f"iii) numerical features have the same scale:\n {numerical_features.describe()}")


# In[6]:


# iv) visualize a pairplot between numerical columns
sns.pairplot(loan_old.select_dtypes(include=['int64', 'float64']), kind="scatter")
plt.show()


# ### c) i) Preprocess the data. 
# 

# In[7]:


# i) records containing missing values are removed
print(f"i) records containing missing values before removing at loan_old: \n{loan_old.shape}")
loan_old.dropna(inplace=True)
print(f"i) records containing missing values are removed at loan_old: \n{loan_old.shape}")
print(f"i) missing values in loan old after removing:\n {loan_old.isnull().sum()}")
print()
print(f"i) records containing missing values before removing at loan_new: \n{loan_new.shape}")
loan_new.dropna(inplace=True)
print(f"i) records containing missing values are removed at loan_new: \n{loan_new.shape}")
print(f"i) missing values in loan new after removing:\n {loan_new.isnull().sum()}")


# In[8]:


# ii) the features and targets are separated
x_train = loan_old.iloc[:, :-2]
y_train = loan_old.iloc[:, -2:]
print(f"ii) x_train shape: {x_train.shape}")
print(f"    y_train shape: {y_train.shape}")


# In[9]:


# iii) the data is shuffled and splitted into training and testing sets
x_train, x_test, y_train, y_test = train_test_split(x_train, y_train, test_size=0.2, random_state=0)
print(f"iii) x_train shape: {x_train.shape}")
print(f"     y_train shape: {y_train.shape}")
print(f"     x_test  shape: {x_test.shape}")
print(f"     y_test  shape: {y_test.shape}")


# In[10]:


# iv) categorical features are encoded
lable_encoder = LabelEncoder()

# loan old
categorical_columns_train = x_train.select_dtypes(include=['object']).columns
x_train[categorical_columns_train[1:]] = x_train[categorical_columns_train[1:]].apply(lable_encoder.fit_transform)
print(f"iv) x_train after encode:\n {x_train}\n")

categorical_columns_test = x_test.select_dtypes(include=['object']).columns
x_test[categorical_columns_test[1:]] = x_test[categorical_columns_test[1:]].apply(lable_encoder.fit_transform)
print(f"    x_test after encode:\n {x_test}\n")

# loan new
categorical_columns_predict = loan_new.select_dtypes(include=['object']).columns
loan_new[categorical_columns_predict[1:]] = loan_new[categorical_columns_predict[1:]].apply(lable_encoder.fit_transform)
print(f"new data after encoding:\n {loan_new}")

categorical_columns_train, categorical_columns_test, categorical_columns_predict


# In[11]:


# v) categorical targets are encoded
categorical_columns_train = y_train.select_dtypes(include=['object']).columns
y_train[categorical_columns_train] = y_train[categorical_columns_train].apply(lable_encoder.fit_transform)
print(f"v) y_train after encode:\n {y_train}\n")

categorical_columns_test = y_test.select_dtypes(include=['object']).columns
y_test[categorical_columns_test] = y_test[categorical_columns_test].apply(lable_encoder.fit_transform)
print(f"   y_test after encode:\n {y_test}\n")

categorical_columns_train, categorical_columns_test


# In[12]:


#vi) numerical features are standardized
scaler = StandardScaler()
numerical_columns_train = x_train.select_dtypes(include=['int64', 'float64']).columns
x_train[numerical_columns_train] = scaler.fit_transform(x_train[numerical_columns_train])
print(f"vi) x_train after standardized:\n {x_train}\n")

numerical_columns_test = x_test.select_dtypes(include=['int64', 'float64']).columns
x_test[numerical_columns_test] = scaler.fit_transform(x_test[numerical_columns_test])
print(f"    x_test after standardized:\n {x_test}\n")

x_predict = loan_new
numerical_columns_predict = x_predict.select_dtypes(include=['int64', 'float64']).columns
x_predict[numerical_columns_predict] = scaler.fit_transform(x_predict[numerical_columns_predict])
print(f"    x_predict after standardized:\n {x_predict}\n")

numerical_columns_train, numerical_columns_test, numerical_columns_predict


# In[13]:


# convert others to numpy
x_train = x_train.to_numpy().reshape((-1,10))
y_train = y_train.to_numpy().reshape((-1,2))
x_test = x_test.to_numpy().reshape((-1,10))
y_test = y_test.to_numpy().reshape((-1,2))
x_predict = x_predict.to_numpy().reshape((-1,10))


# In[14]:


print(f" x_train shape: {x_train.shape}")
print(f" y_train shape: {y_train.shape}")
print(f" x_test shape: {x_test.shape}")
print(f" y_test shape: {y_test.shape}")
print(f" x_predict shape: {x_predict.shape}")
type(x_train), type(y_train), type(x_test), type(y_test), type(x_predict)


# # Linear Regression
# ### d) Fit a linear regression model to the data to predict the loan amount.
# Use sklearn's linear regression.

# In[15]:


from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error, r2_score


# In[16]:


linear_model = LinearRegression()
x_train_linear = x_train[:, 1:]
y_train_linear = y_train[:, 0]
x_test_linear = x_test[:, 1:]
y_test_linear = y_test[:, 0]
# fit the model with train set
linear_model.fit(x_train_linear, y_train_linear)
print(f"Coefficients: \n {linear_model.coef_} {linear_model.intercept_} \n")

# predict on test set
y_pred = linear_model.predict(x_test_linear)
# print('Mean squared error: %.2f \n' %mean_squared_error(y_test_linear, y_pred))
# print(f"Y Prediction: \n {y_pred} \n")
# print(f"Y Test: \n {y_test_linear} \n")

y_pred_frame = pd.DataFrame({'Loan ID': x_test[:, 0], 'Actual Loan Amount': y_test_linear, 'Predicted Loan Amount': y_pred})
print(y_pred_frame)


# ### e) Evaluate the linear regression model using sklearn's R2 score.

# In[17]:


# Evaluate using r2 score
r2 = r2_score(y_test_linear, y_pred)
print(f"R2 Score: {r2} \n")


# ### j) Use your models on 'loan_new' data to predict the loan amounts.

# In[18]:


x_predict_new = x_predict[:, 1:]
y_pred_new = linear_model.predict(x_predict_new)
#print(f"Y New Prediction: \n {y_pred_new} \n")
predicted_amount = pd.DataFrame({'Loan ID': x_predict[:, 0], 'Loan Amount': y_pred_new})
print(predicted_amount)


# # Logistic Regression
# ### f) Fit a logistic regression model to the data to predict the loan status.
# Implement logistic regression from scratch using gradient descent.

# In[19]:


class LogisticRegression:
    # sigmoid function
    def sigmoid(z):
        return (1/(1+np.exp(-z)))


    # cost function
    def cost_function(theta, x, y):
        m = x.shape[0]
        total_cost = 0.0
        # iterate over all records
        for i in range(m):
            # calculate g(z)
            z = np.dot(x[i], theta)
            y_pred = LogisticRegression.sigmoid(z)
            # calculate total cost
            total_cost += (y[i] * np.log(y_pred) + (1 - y[i]) * np.log(1 - y_pred))
        
        # get average of total cost
        total_cost = -(1 / m) * total_cost
        return total_cost


    # gradient descent function
    def gradient_descent(theta, x, y, alpha = 0.01): 
        # initiate dtheta by zeros
        dtheta = np.zeros(theta.shape)
        m, n = x.shape
    
        # iterate over all dataset records to get derivative
        for i in range(m):
            # calculate g(z)
            z = np.dot(x[i], theta)
            y_pred = LogisticRegression.sigmoid(z)
            # iterate over all features to apply equation of deravitave
            for j in range(n):
                dtheta[j] += (y_pred - y[i])*x[i][j]
            
        # get average of deravitave of theta     
        dtheta = dtheta/m
        # update theta
        theta -= alpha*dtheta
        
        return theta

    # predict new values
    def predict(theta, x_test):
        m = x_test.shape[0]
        y_test_pred = np.zeros(m)
        # iterate over all dataset records to get derivative
        for i in range(m):
            # calculate g(z)
            z = np.dot(x_test[i], theta)
            y_pred = LogisticRegression.sigmoid(z)
            # predict new y based on g(z) result
            y_test_pred[i] = 1 if y_pred >= 0.5 else 0
        
        return y_test_pred


# Test our model

# In[20]:


# our data
x_train_logistic = x_train.copy()
y_train_logistic = y_train[:, 1]
x_test_logistic = x_test.copy()
y_test_logistic = y_test[:, 1]
# to initiate x0 = 1
x_train_logistic[:, 0] = np.ones(x_train_logistic.shape[0])
x_test_logistic[:, 0] = np.ones(x_test_logistic.shape[0])

# calc theta 
theta = np.zeros(x_train_logistic.shape[1])
for i in range(2000): # iterate 1000 to update theta as best as possible
    theta = LogisticRegression.gradient_descent(theta, x_train_logistic, y_train_logistic, 0.01)
print(f"Theta : {theta}\n")

# calc cost function
cost = LogisticRegression.cost_function(theta, x_train_logistic, y_train_logistic)
# print(f"Cost : {cost}\n")


# ### g) Write a function (from scratch) to calculate the accuracy of the model.

# In[21]:


def calc_accuracy(y_test, y_predicted):
    cnt = 0
    for i in range(len(y_test)):
        if y_test[i] == y_predicted[i]:
            cnt += 1
    return cnt / len(y_test)

y_pred = LogisticRegression.predict(theta, x_test_logistic)
acc = calc_accuracy(y_test_logistic, y_pred)
print(f"Logistic model accuracy = {acc} \n")
predicted_status = pd.DataFrame({'Loan ID': x_test[:, 0],'Actual Loan Status': y_test_logistic, 
                                 'Predicted Loan Status': y_pred})
print(predicted_status)


# ### j) Use your models on this data to predict the loan status.

# In[22]:


x_predict_new = x_predict.copy()
x_predict_new[:, 0] = np.ones(x_predict_new.shape[0]) # to initiate x0 = 1

# predict new data
y_pred_new = LogisticRegression.predict(theta, x_predict_new)
#print(f"Y New Prediction: \n {y_pred_new} \n")
predicted_status = pd.DataFrame({'Loan ID': x_predict[:, 0], 'Loan Status': y_pred_new})
print(predicted_status)


# In[ ]:




