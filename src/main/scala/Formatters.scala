import java.time.{Instant, ZoneOffset, OffsetDateTime}
import java.time.format.DateTimeFormatter
import os.list

object Formatters {

  // Pure function to format posts from a subscription
  def formatSubscription(
    url: String, 
    posts: List[Main.Post], 
    ups: Int, 
    ocurr: List[(String, Int)],
  ): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"
    val formattedPosts = posts.map(formatPost).mkString("\n\n")
    val ocurrFormatted = formatOcurr(ocurr)
    val score = formatScore(ups)
    header + "\n" + formattedPosts + "\n\n" + score + "\n\n" + ocurrFormatted
  }

  private def formatOcurr(ocurr: List[(String, Int)]): String = {
    val header = "Words Frequency:"
    val count = 
    if (ocurr.isEmpty) "No words to count"
    else ocurr.map{case (word, count) => s"$word: $count"}.mkString("\n")
    s"$header\n\n$count"
  }

  private def formatScore(ups: Int): String = {
    s"${"=" * 30} Total score of Sub: ${ups} ${"=" * 30}"
  }

  private def formatPost(post: Main.Post): String = {
    val (_, title, selftext, formattedDate, _, urlPost) = post
    s"$title\n$formattedDate\n$urlPost\n\n$selftext\n${"=" * 80}"
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
  private val stopwords = Set("the", "about", "above", "after", "again", "against", "all", "am", "an",
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
  // Extracts word tokens while preserving valid internal separators.
  private val wordPattern = "[A-Za-z0-9]+(?:['-][A-Za-z0-9]+)*".r

  private def extractRelevantWords(post: Main.Post): List[String] = {
    val (_, title, selftext, _, _, _) = post
    val text = s"$title $selftext"
    // Normalizes curly apostrophes and non-breaking spaces before tokenization.
    val normalizedText = text
      .replace('\u2019', '\'')
      .replace('\u2018', '\'')
      .replace('\u00A0', ' ')

    wordPattern
      .findAllIn(normalizedText)
      .toList
      .filter(word => word.nonEmpty && word.head.isUpper)
      .map(_.toLowerCase)
      .filter(word => !stopwords.contains(word))
  }

  def countWords(post: Main.Post): List[(String, Int)] =
    extractRelevantWords(post)
      .groupBy(word => word)
      .view
      .mapValues(_.size)
      .toList
      .sortBy { case (_, count) => -count }

  def countWordsBySubreddit(posts: List[Main.Post]): List[(String, Int)] =
    posts
      .flatMap(extractRelevantWords)
      .groupBy(word => word)
      .view
      .mapValues(_.size)
      .toList
      .sortBy { case (_, count) => -count }
}

object Score {                                            // sumo un nuevo objeto
  def countVotes(posts: List[Main.Post]) : Int = {
    val nums : List[Int] = posts.map { elem => elem._5 }  // guardo en nums una lista de los puntajes 
    nums.foldLeft(0)((x,y) => x + y )                     // para luego usar foldLeft y sumarlos
  }
}
/* 
groupBy devuelve Map[String, List[String]], por ejemplo, Map("scala"-> List(scala, scala, scala), "reddit"-> List(reddit, reddit))
con mapValue(_.size) pasaria a Map("scala" -> 3, "reddit"-> 2)
con toList queda List(("scala", 3), ("reddit", 2))
 */
