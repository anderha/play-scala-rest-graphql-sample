package testutils

object Defaults {
  val todoRoute                       = "/v1/todo"
  val graphQlEndpoint                 = "/graphql"
  val millis                          = 1621082427626L
  val titleOfRESTTodo                 = "REST Testtodo"
  val titleOfRESTTodoUpdated          = "REST Testtodo Updated"
  val descriptionOfRESTTodo           = "This is a REST test!"
  val descriptionOfRESTTodoUpdated    = "This is a REST test updated!"
  val titleOfGraphQLTodo              = "GraphQL Testtodo"
  val titleOfGraphQLTodoUpdated       = "GraphQL Testtodo Updated"
  val descriptionOfGraphQLTodo        = "This is a GraphQL test!"
  val descriptionOfGraphQLTodoUpdated = "This is a GraphQL test updated!"
  val todoGQLSchema                   = s"""
                         | id
                         | title
                         | description
                         | isDone
                         | doneAt
                         | createdAt
                         |""".stripMargin
}
