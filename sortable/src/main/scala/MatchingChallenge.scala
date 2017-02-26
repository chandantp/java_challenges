import java.io._
import java.nio.file._
import net.liftweb.json._

object MatchingChallenge {

  implicit val formats = DefaultFormats
  case class Product(product_name: String, manufacturer: String, model: String, family: Option[String], `announced-date`:String)
  case class Listing(title: String, manufacturer: String, currency: String, price: String)
  case class ProductListings(product_name: String, listings: Array[Listing])

  def main(args: Array[String]) {

    def readProducts = {
      val lines = io.Source.fromFile("products.txt", "utf-8").getLines
      lines.map(parse(_).extract[Product]).toList
    }

    def readListings = {
      val lines = io.Source.fromFile("listings.txt", "utf-8").getLines
      lines.map(parse(_).extract[Listing]).toList
    }

    val products = readProducts
    var listings = readListings

    def productListings(products: List[Product], partialListings: List[ProductListings]): List[ProductListings] = products match {
      case Nil => partialListings
      case product :: remainder => {
        val plistings = product.family match {
          case Some(family) => listings.filter(_.manufacturer.contains(product.manufacturer))
              .filter(_.title.contains(product.family.mkString))
              .filter(_.title.contains(product.model))
          case None => listings.filter(_.manufacturer.contains(product.manufacturer)).filter(_.title.contains(product.model))
        }
        listings = listings.diff(plistings)
        productListings(remainder, ProductListings(product.product_name, plistings.toArray) :: partialListings)
      }
    }

    val prodListings = productListings(products.filter(_.family != None), Nil) ++ productListings(products.filter(_.family == None), Nil)
    val resultFile = Paths.get(".", "results.txt").normalize.toAbsolutePath.toString
    val writer = new OutputStreamWriter(new FileOutputStream(resultFile), "utf-8")
    prodListings.foreach(prodListing => writer.write(compact(render(Extraction.decompose(prodListing))) + System.getProperty("line.separator")))
    writer.close
    println("Generated result = %s".format(resultFile))
  }
}
