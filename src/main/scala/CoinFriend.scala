import kong.unirest.Unirest

import scala.io.Source
import scala.util.Try

object CoinFriend {

  def main(args: Array[String]): Unit = {
    println("foo")
    val token = getToken("token.txt")
    token.foreach(t => fetch(t))
  }

  def getToken(filename: String): Option[String] = {
    Try {
      val bufferedSource = Source.fromFile(filename)
      val token = bufferedSource.getLines().mkString
      bufferedSource.close()
      token
    }.toOption
  }

  def fetch(token: String): Unit = {
    val response = Unirest
      .get("https://rest.coinapi.io/v1/quotes/CHAOEX_SPOT_BTC_USDT/current")
      .header("accept", "application/json")
      .header("X-CoinAPI-Key", token)
      .asJson()
    print(response.getBody.toString)
  }

}
