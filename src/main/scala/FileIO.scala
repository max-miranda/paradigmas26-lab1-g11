import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.time.Instant           
import java.time.OffsetDateTime     
import java.time.ZoneOffset         
import java.time.format.DateTimeFormatter 

object FileIO {

  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(): List[Main.Subscription] = {
    
    implicit val formats = org.json4s.DefaultFormats // para indicar los formatos a trabajar
    
    val source =  Source.fromFile("./subscriptions.json")
    val content = source.mkString

    val subs : List[Main.Subscription] = parse(content).children.map { elem =>
      val name = (elem \ "name").extract[String] // le indico donde acceder y que tipo extraer
      val url = (elem \ "url").extract[String]
      (name, url)
    }
    
    source.close()
    subs
  }

  def downloadFeed(url: String): List[Post]= {
    
    implicit val formats = org.json4s.DefaultFormats //Para poder hacer extract
    val source = Source.fromURL(url) //Obtengo posts de la url 
    val posts = source.mkString //Posts los vuelvo strings
    val jsonObjeto = parse(posts) //Lo hago objeto para indagar en sus campos
    
    val ahorasichicos = (jsonObjeto \ "data" \ "children")

    val out : List[Main.Post] = ahorasichicos.children.map { elem =>
      val subreddit = (elem \ "data" \ "subreddit").extract[String]
      val title = (elem \ "data" \ "title").extract[String]
      val self = (elem \ "data" \ "selftext").extract[String]
      val createdUtc = (elem \ "data" \ "created_utc").extract[Double].toLong
      val date = TextProcessing.formatDateFromUTC(createdUtc)

      Post(subreddit, title, self, date)
      }
      println(out.mkString)
      out      
  }
}
