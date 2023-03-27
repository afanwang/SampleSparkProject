package com.company.bi.export

/**
 * Venue Info object representation for the AdminService Call
 */
case class VenueInfo(id: Option[String] = None, name: Option[String] = None, zip: Option[String] = None, state: Option[String] = None, code: Option[String] = None, timeZone: Option[String] = None, taxRate: Option[String] = None)
