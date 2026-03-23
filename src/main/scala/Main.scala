object Main {

  type Subscription = (String, String) // (subName, url)

  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"

    val subscriptions: List[Subscription] = FileIO.readSubscriptions()

    val allPosts: List[(String, String)] = subscriptions.map { elem =>
      println(s"Fetching posts from: ${elem._1}")
      val posts = FileIO.downloadFeed(elem._2)
      (elem._2, posts)
    }

    val output = allPosts
      .map { case (url, posts) => Formatters.formatSubscription(url, posts) }
      .mkString("\n")

    println(output)
  }
}
