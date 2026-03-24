import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

object FileIO {

  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(): List[Main.Subscription] = {
    implicit val formats = org.json4s.DefaultFormats
    val source =  Source.fromFile("./subscriptions.json")
    val content = source.mkString

    // data.arr.toList.map hace lo siguiente: primero toma la data que en este caso es un JSON,
    // arr sirve para asumir que es un array, luego toList lo vuelve una lista para
    // poder ser mapeado, luego se mapea para acceder correctamente a los elementos de data
    val subs : List[Main.Subscription] = parse(content).children.map { elem =>
      val name = (elem \ "name").extract[String]
      val url = (elem \ "url").extract[String]
      (name, url)
    }
    source.close()
    subs
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
      // val json = ujson.read(source.mkString)
      // val posts  = json("data")("children").arr

      // val test : List[Main.Post] = posts.toList.map { m => 
      //   val subreddit = m("data")("subreddit").str
      //   val title = m("data")("title").str
      //   val selftext = m("data")("selftext").str

      //   val formattedDate = m("data")("created_utc").num.toLong //13052004.0
      //   val date = TextProcessing.formatDateFromUTC(formattedDate)

      //   (subreddit, title, selftext, date)
      // }
    source.mkString
  }
}
