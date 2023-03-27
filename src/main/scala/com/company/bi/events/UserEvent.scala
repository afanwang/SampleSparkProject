package com.company.bi.events


import org.joda.time._

/**
 * User Event data transfer object v1.
 */
case class UserEvent(venueId: String, date: Option[DateTime], idKey: String, appId: Option[String],
                     bundleVersion: Option[String], clientIp: Option[String], deviceId: String, eventType: String,
                     payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent


/**
 * User Event data transfer object v2.
 */
case class UserEventV2(venueId: String, date_shard: String, date: Option[DateTime], idKey: String,
                       appId: Option[String], bundleVersion: Option[String],
                       clientIp: Option[String], deviceId: String, eventType: String,
                       payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent

