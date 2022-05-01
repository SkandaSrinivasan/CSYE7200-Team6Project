# CSYE7200-Team6Project

Spring 2022 Team 6 Project Repository

## Running the Project

### Pre-requisites
1. "training.csv" in src/main/resources.Dataset located at https://www.kaggle.com/datasets/kazanova/sentiment140
2. "vaccination_all_tweets.csv" in src/main/resources.Dataset at https://www.kaggle.com/gpreda/all-covid19-vaccines-tweets
3. Running mongod instance with a database named 'Test'

### Running instructions:
- Run `sbt run` in root directory and select [2] Model
- Once the model has been trained in about ~20min and the pipeline has been saved to disk at `src/main/resources`, run `sbt run` in the root folder again and select [3] Streaming
- Tweets will be streamed to the console and stored in the mongo collection 'tweets' under 'Test' database
- cd into PlayDash/PlayDash and run `sbt run`.Classified Tweets at available at `http://localhost:9000/tweets`
- cd into Frontend
- Drop to a SBT shell using the sbt command
- Inside a SBT shell run the command `plotly/start` to start the frontend app
- Ensure that the backend is running before the frontend
