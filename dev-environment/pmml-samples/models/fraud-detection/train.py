import logging
import sys
import warnings

import numpy as np
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn2pmml import sklearn2pmml
from sklearn2pmml.pipeline import PMMLPipeline

def evaluate_metrics(actual, predicted):
    rmse = np.sqrt(mean_squared_error(actual, predicted))
    mae = mean_absolute_error(actual, predicted)
    r2 = r2_score(actual, predicted)
    return rmse, mae, r2

output_path = sys.argv[1]

if __name__ != "__main__":
    sys.exit()

warnings.filterwarnings("ignore")

# Prepare dataset
try:
    csv_url = ("https://raw.githubusercontent.com/prinz-nussknacker/banksim1/master/bs140513_032310.csv")
    data = pd.read_csv(csv_url, sep=",", quotechar="'", header=0)
except Exception as e:
    logging.exception("Could not read CSV file: {}".format(e))
    exit(1)

data.dropna()
data = data.drop(["step", "customer", "zipcodeOri", "merchant", "zipMerchant"], axis="columns")

# Prepare train and test sets
data_x = data.drop(["fraud"], axis="columns")
data_y = data[["fraud"]]
train_x, test_x, train_y, test_y = train_test_split(data_x, data_y)

# Define pipeline
numeric_features = ['amount']
numeric_transformer = Pipeline(
    steps=[
        ('imputer', SimpleImputer(strategy='median')),
        ('scaler', StandardScaler())])

categorical_features = ['age', 'gender', 'category']
categorical_transformer = OneHotEncoder(handle_unknown='ignore')

preprocessor = ColumnTransformer(
    transformers=[
        ('numeric', numeric_transformer, numeric_features),
        ('categorical', categorical_transformer, categorical_features)])

lr = LogisticRegression(multi_class='ovr')
lr.pmml_name_ = 'FraudDetection'

classifier = PMMLPipeline([
    ('preprocessor', preprocessor),
    ('classifier', lr)
])

# Build model
classifier.fit(train_x, train_y)

# Evaluate model
test_actual = test_y
test_predicted = classifier.predict(test_x)
(rmse, mae, r2) = evaluate_metrics(test_actual, test_predicted)
print("Classifier metrics: MAE={}, RMSE={}, R2={}".format(mae, rmse, r2))

sklearn2pmml(classifier, output_path, with_repr = True)
