import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix, respondWithHeaders}
import doobie.util.ExecutionContexts
//import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import cats.effect._
import doobie.Transactor
import doobie.implicits._
import streamz.converter

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends App {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    "jdbc:postgresql:world", // connect URL (driver-specific)
    "postgres", // user
    "" // password
  )

  def getApplicantList: IO[List[Applicant]] = {
    sql"""select * from applicant
     """.query[Applicant]
      .stream
      .compile
      .toList
      .transact(xa)
  }

  def getApplicants: fs2.Stream[IO, Applicant] = {
    sql"""select * from
    applicant""".query[Applicant]
      .stream
      .transact(xa)
  }

  def getApplicantWithHobbies(id: Int): IO[List[Hobbies]] = {
    sql"""select * from hobbies where applicant_id=$id
      """.query[Hobbies]
      .stream
      .compile
      .toList
      .transact(xa)
  }

  val hobbiesFlow: Flow[Applicant, List[String], NotUsed] = Flow[Applicant].map { applicant =>
    getApplicantWithHobbies(applicant.id).unsafeRunSync() match {
      case x: List[Hobbies] => applicant.parse :+ x.map(_.hobby).mkString(",")
      case _ => applicant.parse()
    }
  }

  val route = pathPrefix("applicant-download") {
    get {
      val source = Source.fromGraph(converter.fs2StreamToAkkaSource(getApplicants))
        .via(hobbiesFlow)
        .map(x => ByteString(x.mkString(",") + "\n"))
      val headers = List(RawHeader("Content-Disposition", s"attachment; filename=applicant.csv"))

      respondWithHeaders(headers) {
        complete(HttpEntity(ContentTypes.`text/csv(UTF-8)`, source))
      }
    }
  }

  implicit lazy val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

  println(s"Server online at http://localhost:8080/applicant-download\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}