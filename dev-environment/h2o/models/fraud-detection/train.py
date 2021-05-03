import os
import sys
import time

import h2o
from h2o.estimators.glm import H2OGeneralizedLinearEstimator


def mark_factor_columns(data, columns):
    for col in columns:
        data[col] = data[col].asfactor()
    return data


def unix_timestamp_millis():
    return round(time.time() * 1000)


def evaluate_metrics(model):
    rmse = model.rmse()
    r2 = model.r2()
    return rmse, r2


if __name__ != "__main__":
    sys.exit()

h2o_port = int(os.environ['H2O_SERVER_PORT'])
h2o.init(port=h2o_port)

# Prepare dataset
try:
    csv_url = ("https://raw.githubusercontent.com/prinz-nussknacker/banksim1/master/bs140513_032310.csv")
    data = h2o.import_file(csv_url, sep=",", header=0)
except Exception as e:
    logging.exception("Could not read CSV file: {}".format(e))
    exit(1)

data.na_omit()
data = data.drop(["step", "customer", "zipcodeOri", "merchant", "zipMerchant"])
data = mark_factor_columns(data, ["age", "gender", "category", "fraud"])

predictors = data.columns
predictors.remove("fraud")
response = "fraud"

# Prepare train and test sets
train, test = data.split_frame(ratios=[0.75])

# Build model
glm = H2OGeneralizedLinearEstimator(family='binomial')
glm.train(x=predictors, y=response, training_frame=train)
glm.model_id = f"fraud-detection-{unix_timestamp_millis()}"

# Evaluate model
(rmse, r2) = evaluate_metrics(glm)
print("FraudDetection model:")
print("  RMSE: {}".format(rmse))
print("  R2: {}".format(r2))

glm.save_mojo("exports")
print(f"Fraud detection model exported as {glm.model_id}.zip")