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

object Filter {
  def filterPosts(posts: List[Main.Post]): List[Main.Post] = { 
    val filtrados = posts.filter(item => item._3.trim.nonEmpty && item._2.trim.nonEmpty) // trim saca espacios, filtro los posts con selftext o title vacios
    filtrados 
  }
}

object Count {
  def countWords(post: Main.Post): List[(String, Int)] = {
    val stopwords = Set("the", "about", "above", "after", "again", "against", "all", "am", "an",
    "and", "any", "are", "aren't", "as", "at", "be", "because", "been",
    "before", "being", "below", "between", "both", "but", "by", "can't",
    "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't",
    "doing", "don't", "down", "during", "each", "few", "for", "from", "further",
    "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd",
    "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
    "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if",
    "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me",
    "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off",
    "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves",
    "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
    "should", "shouldn't", "so", "some", "such", "than", "that", "that's",
    "the", "their", "theirs", "them", "themselves", "then", "there", "there's",
    "these", "they", "they'd", "they'll", "re", "they've", "this", "those",
    "through", "to", "too", "under", "until", "up", "very", "was", "wasn't",
    "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what",
    "what's", "when", "when's", "where", "where's", "which", "while", "who",
    "who's", "whom", "why", "why's", "with", "won't", "would",
    "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
    "yourself", "yourselves")
    val palabras = post._1.split("\\s+").toList                                        // el texto es un string gigante entonces lo divido por espacio en blanco, tab etc
    val mayusculas = palabras.filter(item => item.nonEmpty && item.head.isUpper)       // separo las mayusculas
    val minusculas = mayusculas.map(_.toLowerCase)                                     // las paso a minusculas para poder compararlas con las stopwords
    val sinStopwords = minusculas.filter (item => !stopwords.contains(item))           // filtro stopwords
    val agrupadas = sinStopwords.groupBy(item => item).mapValues(_.size)               // agrupo las palabras y determino cuantas veces se repiten
    val ordenadas = agrupadas.toList.sortBy(item => -item._2)                          // paso de Map a List y orden de mayor ocurrencia a menor  

    ordenadas

  }
}

/* 
groupBy devuelve Map[String, List[String]], por ejemplo, Map("scala"-> List(scala, scala, scala), "reddit"-> List(reddit, reddit))
con mapValue(_.size) pasaria a Map("scala" -> 3, "reddit"-> 2)
con toList queda List(("scala", 3), ("reddit", 2))
 */