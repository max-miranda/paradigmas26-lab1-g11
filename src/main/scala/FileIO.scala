import scala.io.Source
import upickle.default._
import ujson._

object FileIO {

  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(): List[Main.Subscription] = {
    val json =  os.pwd / "src" / ".." / "subscriptions.json"
    val data = ujson.read(os.read(json))

    // data.arr.toList.map hace lo siguiente: primero toma la data que en este caso es un JSON,
    // arr sirve para asumir que es un array, luego toList lo vuelve una lista para
    // poder ser mapeado, luego se mapea para acceder correctamente a los elementos de data
    val subs : List[Main.Subscription] = data.arr.toList.map { elem =>
      val name = elem("name").str
      val url = elem("url").str
      (name, url)
    }
    subs
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
      
      val source = Source.fromURL(url)
      val json = ujson.read(source.mkString)
      val posts  = json("data")("children").arr

      val test : List[Post] = posts.toList.map { m => 
        val subreddit = m("data")("subreddit").str
        val title = m("data")("title").str
        val selftext = m("data")("selftext").str

        val formattedDate = m("data")("created_utc").num.toLong //13052004.0
        val date = TextProcessing.formatDateFromUTC(formattedDate)

        (subreddit, title, selftext, date)
      }
      
    
    test.toString
  }
}
