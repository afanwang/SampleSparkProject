package com.bi.events

import java.util.UUID

import org.joda.time._

case class StaffEvent(venueId: String, date: Option[DateTime], idKey: Option[UUID], appId: Option[String],
                      clientIp: Option[String], eventType: Option[String], payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent


/**
 * Staff Event Data Transfer Object v2.
 */
case class StaffEventV2(venueId: String, dateShard: String, date: Option[DateTime], idKey: Option[UUID], appId: Option[String],
                        clientIp: Option[String], eventType: Option[String], payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent
