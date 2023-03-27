package com.bi.events

import java.util.Date

import org.joda.time._

/**
 * Device events schema v1.
 */
case class DeviceEvent(venueId: String, date: Option[Date], idKey: String, appId: Option[String], bundleVersion: Option[String],
                       clientIp: Option[String], deviceId: String, eventType: String, payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent


/**
 * Device events schema v2.
 */
case class DeviceEventV2(venueId: String, dateShard: String, date: Option[Date], idKey: String, appId: Option[String], bundleVersion: Option[String],
                         clientIp: Option[String], deviceId: String, eventType: String, payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent


case class DeviceEventES(venueId: String, venueName: Option[String], chain: Option[String], deviceType: Option[String], date: Option[String], time: Option[String],
                         localTimeStamp: Option[DateTime], timestamp: Option[Date], appId: Option[String], bundleVersion: Option[String],
                         clientIp: Option[String], mac: String, eventType: String, payload: Option[Map[String, Any]], userId: Option[String], location: Option[Seq[Double]]) extends VenueEvent