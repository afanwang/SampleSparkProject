package com.company.bi.export.elasticsearch

/**
 * Chain info
 */
case class ChainInfo(id: Option[String], name: Option[String], url: Option[String], venueIds: Option[Seq[String]])
