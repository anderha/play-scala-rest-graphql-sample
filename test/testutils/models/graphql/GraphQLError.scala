package testutils.models.graphql

import play.api.libs.json.{ Json, OFormat }

case class GraphQLError(message: String, locations: Seq[GraphQlErrorLocation])

object GraphQLError {
  implicit val format: OFormat[GraphQLError] = Json.format[GraphQLError]
}

case class GraphQlErrorLocation(line: Int, column: Int)

object GraphQlErrorLocation {
  implicit val format: OFormat[GraphQlErrorLocation] = Json.format[GraphQlErrorLocation]
}
