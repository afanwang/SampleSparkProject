package com.company.bi.export.elasticsearch.processor

import com.company.bi.export._
import com.company.bi.export.elasticsearch._
import com.company.bi.migration.config._
import grizzled.slf4j._
import org.apache.http.client.methods._
import org.apache.http.impl.client._
import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * Retrieve venue info from web admin.
 */
class AdminInfoService extends Logging {

  lazy val endPointURL: String = ApplicationConfig.web101ApiURL

  def getVenueInfo(venueId: String) = {
    implicit val formats = DefaultFormats
    val content = AdminInfoService.getRestContent(s"${endPointURL}venue/$venueId")
    val venueInfo: VenueInfo = parse(content).extract[VenueInfo]
    logger.debug(venueInfo)
    venueInfo
  }

  def getChainInfo(venueId: String) = {
    implicit val formats = DefaultFormats
    val content = AdminInfoService.getRestContent(s"${endPointURL}chain/$venueId")
    try {
      val chainInfo: ChainInfo = parse(content).extract[ChainInfo]
      logger.debug(chainInfo)
      chainInfo
    } catch {
      case e: ParserUtil.ParseException => {
        logger.debug("Chain info parsing failed:" + content)
        ChainInfo(Some("0"), Some("undefined"), None, None)
      }
    }
  }
}

object AdminInfoService {

  def apply() = new AdminInfoService()

  def getRestContent(url: String): String = {
    val httpClient = new DefaultHttpClient()
    val httpResponse = httpClient.execute(new HttpGet(url))
    val entity = httpResponse.getEntity
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent
      content = scala.io.Source.fromInputStream(inputStream).getLines().mkString
      inputStream.close()
    }
    httpClient.getConnectionManager.shutdown()
    content
  }

}
