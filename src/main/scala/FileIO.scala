import scala.io.Source
import scala.util.Using
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.time.Instant           
import java.time.OffsetDateTime     
import java.time.ZoneOffset         
import java.time.format.DateTimeFormatter 

object FileIO {

  private val redditUrl = "https://www.reddit.com" // agrego prefijo para el link final del post
  
  def readSubscriptions(): Option[List[Main.Subscription]] = {

    implicit val formats = org.json4s.DefaultFormats // para indicar los formatos a trabajar

    try {
      Some(
        Using.resource(Source.fromFile("./subscriptions.json")) { source =>
          val content = source.mkString

          parse(content).children.map { elem =>
            val name = (elem \ "name").extract[String] // le indico donde acceder y que tipo extraer
            val url = (elem \ "url").extract[String]
            (name, url)
          }
        }
      )
    } catch {
      case _: Exception => None
    }
  }

  def downloadFeed(url: String): (Option[List[Main.Post]])= {
    
    implicit val formats = org.json4s.DefaultFormats //Para poder hacer extract

    try {
      Some(
        Using.resource(Source.fromURL(url)) { source =>
          val posts = source.mkString //Posts los vuelvo strings
          val jsonObjeto = parse(posts) //Lo hago objeto para indagar en sus campos
          
          val postEntries = (jsonObjeto \ "data" \ "children" \ "data")

          postEntries.children.map { elem =>
            val subreddit = (elem \ "subreddit").extract[String]
            val title = (elem \ "title").extract[String]
            val self = (elem \ "selftext").extract[String]
            val createdUtc = (elem \ "created_utc").extract[Double].toLong
            val date = TextProcessing.formatDateFromUTC(createdUtc)
            val ups = (elem \ "ups").extract[Int]
            val postUrl = redditUrl + (elem \ "permalink").extract[String]

            (subreddit, title, self, date, ups, postUrl)
          }
        }
      )
    } catch {
      case _: Exception => None
    }
  }
}
