package testutils.models.graphql

import play.api.libs.json.{ Json, OFormat }

case class GraphQLErrorResponse(errors: Seq[GraphQLError])

object GraphQLErrorResponse {
  implicit val format: OFormat[GraphQLErrorResponse] = Json.format[GraphQLErrorResponse]
}
