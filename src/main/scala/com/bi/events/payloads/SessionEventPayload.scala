package com.bi.events.payloads

/**
 * Session Event Payload.
 *
 * User_events message marked as session
 */
case class SessionEventPayload(action: Option[String], sessionStart: Option[Long], userId: Option[String])