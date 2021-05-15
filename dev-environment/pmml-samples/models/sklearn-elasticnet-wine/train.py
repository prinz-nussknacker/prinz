# Example based on example from mlflow repository https://github.com/mlflow/mlflow
# The data set used in this example is from http://archive.ics.uci.edu/ml/datasets/Wine+Quality
# P. Cortez, A. Cerdeira, F. Almeida, T. Matos and J. Reis.
# Modeling wine preferences by data mining from physicochemical properties.
# In Decision Support Systems, Elsevier, 47(4):547-553, 2009.

import logging
import warnings
import sys
import pandas as pd
import numpy as np
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.linear_model import ElasticNet
from sklearn2pmml import sklearn2pmml
from sklearn2pmml.pipeline import PMMLPipeline

alpha = float(sys.argv[1])
l1_ratio = float(sys.argv[2])
output_path = sys.argv[3]
model_id = sys.argv[4]

logging.basicConfig(level=logging.WARN)
logger = logging.getLogger(__name__)


def eval_metrics(actual, pred):
    rmse_metric = np.sqrt(mean_squared_error(actual, pred))
    mae_metric = mean_absolute_error(actual, pred)
    r2_metric = r2_score(actual, pred)
    return rmse_metric, mae_metric, r2_metric


if __name__ != "__main__":
    sys.exit()

warnings.filterwarnings("ignore")
np.random.seed(40)

csv_url = "https://raw.githubusercontent.com/zygmuntz/wine-quality/master/winequality/winequality-red.csv"
try:
    data = pd.read_csv(csv_url, sep=";")
except Exception as e:
    logger.exception("Unable to download training & test CSV, check your internet connection. Error: {}".format(e))
    exit(1)

train, test = train_test_split(data)

train_x = train.drop(["quality"], axis=1)
test_x = test.drop(["quality"], axis=1)
train_y = train[["quality"]]
test_y = test[["quality"]]

lr = ElasticNet(alpha=alpha, l1_ratio=l1_ratio, random_state=42)
lr.pmml_name_ = f"PMML-ElasticnetWineModel-{model_id}"
pipeline = PMMLPipeline(steps=[("elastic_net", lr)])

pipeline.fit(train_x, train_y)
predicted_qualities = pipeline.predict(test_x)

(rmse, mae, r2) = eval_metrics(test_y, predicted_qualities)

print("Elasticnet model (alpha={}, l1_ratio={}):".format(alpha, l1_ratio))
print("  RMSE: {}".format(rmse))
print("  MAE: {}".format(mae))
print("  R2: {}".format(r2))

sklearn2pmml(pipeline, output_path, with_repr=True)
print("Elasticnet model (alpha={}, l1_ratio={}) exported".format(alpha, l1_ratio))
