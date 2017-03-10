import java.io.{File, PrintWriter}
import java.nio.file.Paths
import net.liftweb.json.{compact, parse, render, Extraction, DefaultFormats}

object MatchingChallenge {

  case class Product(product_name: String, manufacturer: String, model: String, family: Option[String], `announced-date`:String)
  case class Listing(title: String, manufacturer: String, currency: String, price: String)
  case class ProductListings(product_name: String, listings: Array[Listing])

  def main(args: Array[String]) {
    implicit val formats = DefaultFormats
    var listings = io.Source.fromFile("listings.txt", "utf-8").getLines.map(parse(_).extract[Listing]).toList
    val products = io.Source.fromFile("products.txt", "utf-8").getLines.map(parse(_).extract[Product]).toList

    def getProductListings(products: List[Product], productList: List[ProductListings]): List[ProductListings] = products match {
      case Nil => productList
      case product :: remainingProducts => {
        // Get listings which have matching product.manufacturer,
        // product.family (if available) and product.model (ignore case)
        val productListings = product.family match {
          case Some(family) => listings
            .filter(_.manufacturer.toLowerCase.contains(product.manufacturer.toLowerCase))
            .filter(_.title.toLowerCase.contains(product.family.mkString.toLowerCase))
            .filter(_.title.toLowerCase.contains(product.model.toLowerCase))
          case None => listings
            .filter(_.manufacturer.toLowerCase.contains(product.manufacturer.toLowerCase))
            .filter(_.title.toLowerCase.contains(product.model.toLowerCase))
        }
        // Remove matching product listings as a single listing can match at most one product
        listings = listings.filterNot(productListings.toSet)
        getProductListings(remainingProducts, ProductListings(product.product_name, productListings.toArray) :: productList)
      }
    }

    def writeToFile(productListings: List[ProductListings]): Unit = {
      val NewLine = System.getProperty("line.separator")
      val resultsFile = Paths.get(".", "results.txt").normalize.toAbsolutePath.toString
      val writer = new PrintWriter(new File(resultsFile))
      try
        productListings.foreach(p => writer.write(compact(render(Extraction.decompose(p))) + NewLine))
      finally
        writer.close
      println("Results file = %s".format(resultsFile))
    }

    val allProductListings = getProductListings(products.filter(_.family != None), Nil) ++
        getProductListings(products.filter(_.family == None), Nil)
    writeToFile(allProductListings)
  }
}
