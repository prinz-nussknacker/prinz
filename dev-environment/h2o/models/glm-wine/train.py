import h2o
import sys
import os
from h2o.estimators.glm import H2OGeneralizedLinearEstimator

if __name__ != "__main__":
    sys.exit()

print(sys.argv)
alpha = float(sys.argv[1])
model_id = int(sys.argv[2])

h2o_port = int(os.environ['H2O_SERVER_PORT'])
h2o.init(port = h2o_port)

csv_url = ("https://raw.githubusercontent.com/zygmuntz/wine-quality/master/winequality/winequality-red.csv")
data = h2o.import_file(csv_url)

predictors = data.columns[:-1]
response = "quality"

train, valid = data.split_frame(ratios = [.8])

glm = H2OGeneralizedLinearEstimator(alpha = .25)
glm.train(x = predictors, y = response, training_frame = train, validation_frame = valid)

print("H2OWineGeneralizedLinearEstimator model (alpha={}):".format(alpha))
print("  RMSE: {}".format(glm.rmse(valid=True)))
print("  MAE: {}".format(glm.mae(valid=True)))
print("  R2: {}".format(glm.r2(valid=True)))

model_path = glm.save_mojo("exports", force=True)
print(f"Wine model exported as {glm.model_id}.zip")
renamed_model_path = model_path.replace(f"{glm.model_id}.zip", f"H2O-ElasticnetWineModel-{model_id}-v0-{model_id}.zip")
os.rename(model_path, renamed_model_path)
