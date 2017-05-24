#### To build locally:
Please start localstack using docker
```
docker run -it -e AWS_CBOR_DISABLE='1' -p 4567-4580:4567-4580 -p 8000:4569 -p 8080:8080 atlassianlabs/localstack
```
``
mvn clean install && java -jar target/dynamo-db-test-1.0-SNAPSHOT.jar
``
