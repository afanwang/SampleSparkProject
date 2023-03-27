package com.company.bi.export.elasticsearch.processor

import com.github.nscala_time.time.Imports._
import com.company.bi.events._
import com.company.bi.export.elasticsearch._
import com.company.bi.migration.processor._
import org.joda.time.format._

/**
 * Device Events Processor for ElasticSearch
 */
class DeviceEventsProcessorES extends EventsProcessor[DeviceEvent, DeviceEventES] {


  /**
   * Define event type I to event type O transformation rules
   * @param event type I event
   * @param venueId venue ID for new event
   * @return
   */
  override def v1ToV2Mapper(event: DeviceEvent, venueId: String): DeviceEventES = {
    val updPayload = event.payload.get.map(processPayload)
    val venueInfo = VenueMeta.getVenueInfoById(venueId)
    val chainInfo = VenueMeta.getChainInfoById(venueId)
    val timeZone: DateTimeZone = DateTimeZone.forID(venueInfo.timeZone.getOrElse("UTC"))
    val dateTime: DateTime = new DateTime(event.date.get, timeZone)
    val dateFormatter = ISODateTimeFormat.basicDate()
    val timeFormatter = ISODateTimeFormat.hourMinute()

    val dateOnly: String = dateFormatter.print(dateTime)

    val timeOnly: String = timeFormatter.print(dateTime)
    new DeviceEventES(event.venueId, venueInfo.name, chainInfo.name, Some("Tablet"), Some(dateOnly), Some(timeOnly), Some(dateTime), Some(event.date.get), event.appId, event.bundleVersion,
      event.clientIp, event.deviceId, event.eventType, Some(updPayload), event.userId, createCoordinates(event.payload))
  }

  private def processPayload(pl: (String, String)): (String, Any) = {
    pl._1 match {
      case "ramMemoryUsagePercentage" => "memory" -> pl._2.toDouble.floor
      case "cpuUsagePercentage" => "cpu" -> pl._2.toDouble.floor
      case "batteryHealth.capacityPercentage" => "batteryPercentage" -> pl._2.toDouble.floor
      case "batteryHealth.temperature" => "batteryTemperature" -> pl._2.toDouble.floor
      case "batteryHealth.healthStatus" => "batteryHealthStatus" -> pl._2
      case "extra" | "networkStat.name" => "ssid" -> pl._2
      case "networkStat.level" => "WiFiStrength" -> pl._2.toInt
      case "uptimeInMillis" => "uptime" -> millisToHours(pl._2.toDouble)
      case _ => pl
    }
  }

  private def createCoordinates(payloadIn: Option[Map[String, String]]): Option[Seq[Double]] = {
    val payload: Map[String, String] = payloadIn.get
    (payload.get("latitude"), payload.get("longitude")) match {
      case (Some(lat), Some(lon)) => Some(Seq(lon.toDouble, lat.toDouble)) // [lon, lat] according the the http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-point-type.html#_lat_lon_as_array_5
      case (_, _) => None
    }
  }

  private def millisToHours(millis: Double): Double = {
    val hours = millis * 0.00001 / 36
    ("%1.2f" format hours).toDouble
  }
}
