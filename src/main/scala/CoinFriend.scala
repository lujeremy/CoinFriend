import java.io.{BufferedWriter, File, FileWriter}

import kong.unirest.Unirest
import org.slf4j.{Logger, LoggerFactory}
import spark.SparkSessionWrapper

import scala.io.Source
import scala.util.{Failure, Success, Try}

object CoinFriend extends SparkSessionWrapper {

  implicit val LOGGER: Logger = LoggerFactory.getLogger(this.getClass.getName)

  def main(args: Array[String]): Unit = {
    getToken("token.txt") match {
      case Success(token) => fetchAssets(token)
      case Failure(e)     => LOGGER.error(s"Token error: ${e.getMessage}")
    }

    val df = spark.read.json("assets.json")
    df.select("asset_id").show(5)
  }

  def getToken(filename: String): Try[String] =
    Try {
      val bufferedSource = Source.fromFile(filename)
      val token = bufferedSource.getLines().mkString
      bufferedSource.close()
      token
    }

  def fetchAssets(token: String): Unit = {
    val response = Unirest
      .get("https://rest.coinapi.io/v1/assets")
      .header("accept", "application/json")
      .header("X-CoinAPI-Key", token)
      .asJson()

    val file = new File("assets.json")

    if (!file.exists()) {
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(response.getBody.toString)
      bw.close()
      LOGGER.info(s"Asset information written to ${file.getName}")
    }
  }

}
