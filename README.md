# TodoApp - Rest & GraphQL Sample

## Architecture

![](./doc/architecture.png)

## Starting the webservice

```bash
# Start your postgres instance
cd ./local-runner
docker compose up -d
cd ..
# migrate database
sbt flyway/flywayMigrate
# generate code for the database
sbt slickGen
# generate code for tests
sbt graphqlCodegen
# start the webservice
sbt run
```

## Testing

### Postman

- open your postman installation
- import the postman collection from [.\doc\GraphQL-TodoApp-Testchain.postman_collection.json](./doc/GraphQL-TodoApp-Testchain.postman_collection.json)
- make sure you have started the webservice
- run the collection  
  ![](./doc/postman-run-collection.png)

### ScalaTest + Play

```bash
# Make sure the database is down if you have run the server before
# Windows
"./.test/runtest.bat"
# Unix/Mac
./.test/runtest.sh
```

## Schema Validation

- validation with [GraphQL Inspector](https://github.com/kamilkisiela/graphql-inspector)
- generate schema with:
  ```bash
  sbt graphqlSchemaGen
  ```
  output is saved in [schema.graphql](./schema.graphql)
- github action configuration is found in [.github/schema-validation.yml](./.github/workflows/schema-validation.yml)
- schema is validated on pull request to master
- validation will fail on breaking changes
- use label 'expected-breaking-change' to mark a pull request when breaking changes are expected, e.g. [https://github.com/anderha/todo-rest-graphql-sample/pull/3](https://github.com/anderha/todo-rest-graphql-sample/pull/3)

## Load Testing

- the load test is carried out with [easygraphql-load-tester](https://github.com/EasyGraphQL/easygraphql-load-tester) and [Artillery](https://artillery.io/)
- make sure the webservice is up and running

```bash
cd ./load-testing
# install dependencies
npm i
# run load test
npm run load-test
```

## Based on

- [bootstrap-play2](https://github.com/innFactory/bootstrap-play2)