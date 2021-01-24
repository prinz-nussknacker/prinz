import logging
import sys
import warnings

import mlflow
import mlflow.sklearn
import numpy as np
import pandas as pd
from mlflow.models import ModelSignature
from mlflow.types import ColSpec, Schema
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler


def evaluate_metrics(actual, predicted):
    rmse = np.sqrt(mean_squared_error(actual, predicted))
    mae = mean_absolute_error(actual, predicted)
    r2 = r2_score(actual, predicted)
    return rmse, mae, r2

if __name__ != "__main__":
    sys.exit()

warnings.filterwarnings("ignore")
model_id = int(sys.argv[2])

# Prepare dataset
try:
    csv_url = ("https://raw.githubusercontent.com/prinz-nussknacker/banksim1/master/bs140513_032310.csv")
    data = pd.read_csv(csv_url, sep=",", quotechar="'", header=0)
except Exception as e:
    logger.exception("Could not read CSV file: {}".format(e))
    exit(1)

data.dropna()
data = data.drop(["step", "customer", "zipcodeOri", "merchant", "zipMerchant"], axis="columns")

input_schema = Schema([
    ColSpec("string", "age"),
    ColSpec("string", "gender"),
    ColSpec("string", "category"),
    ColSpec("double", "amount")
])
output_schema = Schema([
    ColSpec("integer")
])
signature = ModelSignature(inputs=input_schema, outputs=output_schema)

# Prepare train and test sets
data_x = data.drop(["fraud"], axis="columns")
data_y = data[["fraud"]]
train_x, test_x, train_y, test_y = train_test_split(data_x, data_y)

with mlflow.start_run():

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

    classifier = Pipeline(
        steps=[
            ('preprocessor', preprocessor),
            ('classifier', LogisticRegression())])

    # Build model
    classifier.fit(train_x, train_y)

    # Evaluate model
    test_actual = test_y
    test_predicted = classifier.predict(test_x)
    (rmse, mae, r2) = evaluate_metrics(test_actual, test_predicted)
    print("Classifier metrics: MAE={}, RMSE={}, R2={}".format(mae, rmse, r2))

    mlflow.log_metric("mae", mae)
    mlflow.log_metric("rmse", rmse)
    mlflow.log_metric("r2", r2)

    model_name = "FraudDetection-{}".format(model_id)
    mlflow.sklearn.log_model(
        classifier,
        "model",
        registered_model_name = model_name,
        signature = signature)
