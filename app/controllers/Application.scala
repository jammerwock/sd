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

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.pool.ConnectionPool
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import controllers.Application._
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.i18n.{ I18nSupport, Lang, LangImplicits, Langs }
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

@deprecated
case class CategoryDepr(code: String, name: String)

@deprecated
case class CategoryItemDepr(id: Long, category: CategoryDepr, title: String, image: String, text: String)

case class Category(id: CategoryId, code: CategoryCode, name: CategoryName)

case class Vendor(id: VendorId, code: VendorCode, name: VendorName)

case class Model(id: ModelId, name: ModelName, category: Category, vendor: Vendor)

case class CurrentItem(modelId: ModelId, categoryCode: CategoryCode, vendorCode: VendorCode)

object CurrentItem {
  val empty = CurrentItem(0, "", "")
}

@deprecated
case class ModelItem(menuItem: MenuItem)

@deprecated
case class MenuItem(id: ModelId, modelName: String, categoryName: CategoryName, categoryCode: String, vendorName: VendorName, vendorCode: String)

case class CarouselItem(fileName: FileName)

class Application @Inject() (pool: ConnectionPool[PostgreSQLConnection], mcc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(mcc) with I18nSupport {

  import Application._

  val fooForm = Form(single("foo" -> text(maxLength = 20)))

  val validationForm = Form(tuple(
    "username" -> nonEmptyText(maxLength = 20),
    "email" -> email,
    "age" -> number(min = 18, max = 99),
    "color" -> nonEmptyText.verifying(pattern("^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$".r))
  ))

  def index: Action[AnyContent] = Action.async { implicit request =>

    val eventualMenuData = getMenuData(pool)
    val eventualCarouselData = getCarouselData(pool)
    for {
      menuData <- eventualMenuData
      carouselData <- eventualCarouselData
    } yield Ok(views.html.index(menuData, carouselData))

    //    getMenuData(pool).flatMap(menuData => getCarouselData(pool).map(carouselData =>
    //      Ok(views.html.index(fooForm, menuData, carouselData, validationForm))))

  }

  def showItem(categoryCode: String, vendorCode: String, itemId: Long): Action[AnyContent] = Action.async { implicit request =>
    val eventualMaybeItems: Future[Option[IndexedSeq[Model]]] = pool.sendPreparedStatement(
      """
            SELECT
              m.id AS model_id,
              m.name AS model_name,
              m.category_code,
              m.vendor_code,
              c.id AS category_id,
              c.name AS category_name,
              v.id AS vendor_id,
              v.name AS vendor_name
            FROM models m
              LEFT JOIN categories c ON c.code = m.category_code
              LEFT JOIN vendors v ON v.code = m.vendor_code
            WHERE m.id = ?
                  """.stripMargin, Seq(itemId)
    ).map {
        _.rows.map { resultSet =>
          resultSet.map { rowData =>
            Model(
              rowData("model_id").asInstanceOf[ModelId],
              rowData("model_name").asInstanceOf[ModelName],
              Category(
                rowData("category_id").asInstanceOf[CategoryId],
                rowData("category_code").asInstanceOf[CategoryCode],
                rowData("category_name").asInstanceOf[CategoryName]
              ),
              Vendor(
                rowData("vendor_id").asInstanceOf[VendorId],
                rowData("vendor_code").asInstanceOf[VendorCode],
                rowData("vendor_name").asInstanceOf[VendorName]
              )

            )
          }
        }
      }

    val eventualMenuData = getMenuData(pool)

    for {
      menu <- eventualMenuData
      modelItem <- eventualMaybeItems.map(_.get.head)
    } yield {
      Ok(views.html.item(modelItem, menu, CurrentItem(itemId, categoryCode, vendorCode)))
    }

  }
}

object Application {
  type MenuData = Map[Category, Map[Vendor, List[Model]]]
  type CarouselData = List[CarouselItem]

  type ModelId = Long
  type ModelName = String

  type CategoryId = Long
  type CategoryName = String
  type CategoryCode = String

  type VendorId = Long
  type VendorName = String
  type VendorCode = String

  type FileName = String

  //  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  def getCarouselData(pool: ConnectionPool[PostgreSQLConnection])(implicit ec: ExecutionContext): Future[CarouselData] = {
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

  def getMenuData(pool: ConnectionPool[PostgreSQLConnection])(implicit ec: ExecutionContext): Future[MenuData] = {
    pool.sendPreparedStatement(
      """
        |SELECT
        |  m.id AS model_id,
        |  m.name AS model_name,
        |  m.category_code,
        |  m.vendor_code,
        |  c.id AS category_id,
        |  c.name AS category_name,
        |  v.id AS vendor_id,
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

        val menuData: List[Model] = datas.map(d => d.map(rowData =>

          Model(
            rowData("model_id").asInstanceOf[ModelId],
            rowData("model_name").asInstanceOf[ModelName],
            Category(
              rowData("category_id").asInstanceOf[CategoryId],
              rowData("category_code").asInstanceOf[CategoryCode],
              rowData("category_name").asInstanceOf[CategoryName]
            ),
            Vendor(
              rowData("vendor_id").asInstanceOf[VendorId],
              rowData("vendor_code").asInstanceOf[VendorCode],
              rowData("vendor_name").asInstanceOf[VendorName]
            )

          )
        //        List(
        //          "name" -> rowData("name").asInstanceOf[String],
        //          "vendor_code" -> rowData("vendor_code").asInstanceOf[String],
        //          "category_code" -> rowData("category_code").asInstanceOf[String]
        //        )
        )).get.toList

        //        menuData.groupBy(_.categoryName) // fixme by categoryCode vendorCode

        menuData.groupBy(_.category).map { case (a, b) => a -> b.groupBy(_.vendor) }

      }

  }
}