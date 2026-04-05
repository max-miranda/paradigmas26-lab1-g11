object Main {

  type Subscription = (String, String) // (subName, url)
  type Post = (String, String, String, String) // (subreddit, title, selftext, formattedDate)

  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"

    val subscriptionsOpt: Option[List[Subscription]] = FileIO.readSubscriptions()

    val allPostsOpt: Option[List[(String, List[Post])]] = subscriptionsOpt.map { subscriptions =>
      subscriptions.flatMap { elem =>
        println(s"Fetching posts from: ${elem._1}")
        FileIO.downloadFeed(elem._2).map(posts => (elem._2, posts)).toList
      }
    }

    val output = allPostsOpt
      .map(_.map { case (url, posts) => Formatters.formatSubscription(url, posts) }.mkString("\n"))
      .getOrElse("Could not read subscriptions.")

    println(output)
  }
}
