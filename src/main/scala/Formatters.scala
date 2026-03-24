import java.time.{Instant, ZoneOffset, OffsetDateTime}
import java.time.format.DateTimeFormatter

object Formatters {

  // Pure function to format posts from a subscription
  def formatSubscription(url: String, posts: String): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"
    val formattedPosts = posts.take(80)
    header + "\n" + formattedPosts
  }
}

object TextProcessing {
  
  def formatDateFromUTC(utc: Long): String = {
    val instante: Instant = Instant.ofEpochSecond(utc) //Registra marca de tiempo precisa
    
    val fechaConZona: OffsetDateTime = instante.atOffset(ZoneOffset.UTC)
    val salida: String = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(fechaConZona)

    salida
  }
}