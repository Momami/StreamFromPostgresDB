
case class Applicant(id: Int,
                     first_name: String,
                     surname: String,
                     other_names: Option[String],
                     position: String) {

  def parse(): List[String] =
    List(
      first_name,
      surname,
      other_names.getOrElse(""),
      position)
}

case class Hobbies(applicant_id: String,
                   hobby: String)
