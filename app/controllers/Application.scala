/**
 * Copyright 2015 Adrian Hurtado (adrianhurt)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers

import javax.inject.Inject

import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.pool.ConnectionPool
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import controllers.Application.{ CategoryName, FileName, VendorName, getMenuData }
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.mvc._

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import scala.util.Try

case class Category(code: String, name: String)
case class CategoryItem(id: Long, category: Category, title: String, image: String, text: String)

case class MenuItem(id: Long, modelName: String, categoryName: CategoryName, categoryCode: String, vendorName: VendorName, vendorCode: String)

case class CarouselItem(fileName: FileName)

class Application @Inject() (pool: ConnectionPool[PostgreSQLConnection], mcc: MessagesControllerComponents) extends MessagesAbstractController(mcc) {

  import Application._

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  val fooForm = Form(single("foo" -> text(maxLength = 20)))

  val validationForm = Form(tuple(
    "username" -> nonEmptyText(maxLength = 20),
    "email" -> email,
    "age" -> number(min = 18, max = 99),
    "color" -> nonEmptyText.verifying(pattern("^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$".r))
  ))

  def index = Action.async { implicit request =>

    val eventualMenuData = getMenuData(pool)
    val eventualCarouselData = getCarouselData(pool)
    for {
      menuData <- eventualMenuData
      carouselData <- eventualCarouselData
    } yield Ok(views.html.index(fooForm, menuData, carouselData, validationForm))

    //    getMenuData(pool).flatMap(menuData => getCarouselData(pool).map(carouselData =>
    //      Ok(views.html.index(fooForm, menuData, carouselData, validationForm))))

  }

  //  val catA: Category = Category("cata", "Cat A")
  //  val catB = Category("catb", "Cat B")
  //  val catC = Category("catc", "Cat C")
  //
  //  val item1 = CategoryItem(1, catA, "Domus", "http://via.placeholder.com/50x50", "Girls go with powerdrain at the colorful bridge!")
  //  val item2 = CategoryItem(2, catA, "Extum", "http://via.placeholder.com/50x50", "Cosmonauts die with pattern at the calm parallel universe!")
  //  val item3 = CategoryItem(3, catA, "Caelos", "http://via.placeholder.com/50x50", "Vital, clear collectives cunningly desire a seismic, virtual sun!")
  //  val item4 = CategoryItem(4, catB, "Pulchritudine", "http://via.placeholder.com/50x50", "Sub-light, interstellar crewmates quickly destroy a photonic, devastated species!")
  //  val item5 = CategoryItem(5, catB, "Abactus, era, et decor.", "http://via.placeholder.com/50x50", "Biological, ordinary sonic showers unearthly desire an evasive, mysterious hur'q!")
  //  val item6 = CategoryItem(6, catC, "Hibrida, apolloniates, et parma.", "http://via.placeholder.com/50x50", "This understanding has only been deserved by a greatly exaggerated space suit!")
  //
  //  val cats = List(item1, item2, item3, item4, item5, item6)

  //  def category(code: String) = Action.async { implicit request =>
  //    getMenuData(pool).map(menuData =>
  //      Ok(views.html.category(cats, menuData, code)))
  //  }
  //
  //  def item(code: String, id: Long) = Action.async { implicit request =>
  //
  //    getMenuData(pool).map(menuData =>
  //      Ok(views.html.item(cats, menuData, cats(id.toInt), cats(id.toInt).category.code))
  //    )
  //
  //  }

  //  def vertical = Action { implicit request => Ok(views.html.vertical(fooForm)) }
  //  def horizontal = Action { implicit request => Ok(views.html.horizontal(fooForm)) }
  //  def inline = Action { implicit request => Ok(views.html.inline(fooForm)) }
  //  def mixed = Action { implicit request => Ok(views.html.mixed(fooForm)) }
  //  def readonly = Action { implicit request => Ok(views.html.readonly(fooForm)) }
  //  def multifield = Action { implicit request => Ok(views.html.multifield(fooForm)) }
  //  def extendIt = Action { implicit request => Ok(views.html.extendIt(fooForm)) }
  //  def docs = Action { implicit request => Ok(views.html.docs(fooForm, validationForm)) }

  //  def categories() = Action.async { implicit request =>
  //    getMenuData(pool).map(menuData =>
  //      Ok(views.html.categories(cats, menuData)))
  //  }

}

object Application {
  type MenuData = Map[CategoryName, Map[VendorName, List[MenuItem]]]
  type CarouselData = List[CarouselItem]

  type CategoryName = String
  type VendorName = String
  type FileName = String

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  def getCarouselData(pool: ConnectionPool[PostgreSQLConnection]): Future[CarouselData] = {
    pool.sendQuery("SELECT filename FROM carousel") map { qr =>
      (for {
        rs <- qr.rows
      } yield for {
        rowData <- rs
      } yield {
        CarouselItem(
          rowData("filename").asInstanceOf[String]
        )
      }).get.toList

    }
  }

  def getMenuData(pool: ConnectionPool[PostgreSQLConnection]): Future[MenuData] = {
    pool.sendPreparedStatement(
      """
        |SELECT
        |  m.id AS model_id,
        |  m.name AS model_name,
        |  m.category_code,
        |  m.vendor_code,
        |  c.name AS category_name,
        |  v.name AS vendor_name
        |FROM models m
        |  LEFT JOIN categories c ON c.code = m.category_code
        |  LEFT JOIN vendors v ON v.code = m.vendor_code
      """.stripMargin).map { qr =>
        val datas: Option[IndexedSeq[RowData]] = for {
          rs <- qr.rows
        } yield for {
          data <- rs
        } yield { data }

        val menuData: List[MenuItem] = datas.map(d => d.map(rowData =>

          MenuItem(
            rowData("model_id").asInstanceOf[Long],
            rowData("model_name").asInstanceOf[String],
            rowData("category_name").asInstanceOf[CategoryName],
            rowData("category_code").asInstanceOf[String],
            rowData("vendor_name").asInstanceOf[VendorName],
            rowData("vendor_code").asInstanceOf[String]
          )
        //        List(
        //          "name" -> rowData("name").asInstanceOf[String],
        //          "vendor_code" -> rowData("vendor_code").asInstanceOf[String],
        //          "category_code" -> rowData("category_code").asInstanceOf[String]
        //        )
        )).get.toList

        //        menuData.groupBy(_.categoryName) // fixme by categoryCode vendorCode

        menuData.groupBy(_.categoryName).map { case (a, b) => a -> b.groupBy(_.vendorName) }

      }

  }
}
