import h2o
import sys
import os
from h2o.estimators.glm import H2OGeneralizedLinearEstimator

if __name__ == "__main__":
    h2o_port = int(os.environ['H2O_SERVER_PORT'])
    h2o.init(port = h2o_port)

    csv_url = ("http://archive.ics.uci.edu/ml/machine-learning-databases/wine-quality/winequality-red.csv")
    try:
        data = h2o.import_file(csv_url)
    except Exception as e:
        logger.exception("Unable to download training & test CSV, check your internet connection. Error: {}".format(e))

    predictors = data.columns[:-1]
    response = "quality"

    train, valid = data.split_frame(ratios = [.8])

    alpha = float(sys.argv[1])

    glm = H2OGeneralizedLinearEstimator(alpha = .25)
    glm.train(x = predictors, y = response, training_frame = train, validation_frame = valid)

    print("H2OGeneralizedLinearEstimator model (alpha={}):".format(alpha))
    print("  RMSE: {}".format(glm.rmse(valid=True)))
    print("  MAE: {}".format(glm.mae(valid=True)))
    print("  R2: {}".format(glm.r2(valid=True)))

    glm.save_mojo("exports")
