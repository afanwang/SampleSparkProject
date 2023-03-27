package com.bi.events

import java.util.UUID

import org.joda.time._

case class ServerEvent(venueId: String, date: Option[DateTime], idKey: Option[UUID], appId: Option[String],
                       clientIp: Option[String], eventType: Option[String], payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent

case class ServerEventV2(venueId: String, dateShard: String, date: Option[DateTime], idKey: Option[UUID], appId: Option[String],
                         clientIp: Option[String], eventType: Option[String], payload: Option[Map[String, String]], userId: Option[String]) extends VenueEvent

//INSERT INTO server_events(venue_id,date,id_key,app_id,client_ip,event_type,payload,user_id) VALUES ('undefined',1393525688433,e83d4d80-9fdc-11e3-8f0a-0200073d55db,null,'undefined','menu_successfully_imported',{'Total categories':'24','Total drink components':'0','Total drinks':'69','Total fillers':'6','Total food':'101','Total images':'62','Total products':'170','Venue id':'129f2054-2b4f-4a23-b6b0-1927287222ee','Venue name':'GameLoft - Publisher Test '},null);
