
### What is this ?

simple project to demonstrate Kafka with schema enforcement

### How does it work ?

- the build generates the Quotes.class using the json schema plugin
- the app then generates quotes and publishes them
- half of them do not conform to the schema so should fail

### How to run

- docker compose up
- go to control-center localhost:9021
- navigate to topics
- create a topic called quotes
- select the topic and go to schema
- add the schema from src/schema
- now run Main to see it sending messages with some failing.