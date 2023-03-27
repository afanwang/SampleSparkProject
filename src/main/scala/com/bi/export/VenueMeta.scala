package com.bi.export.elasticsearch

import com.bi.export._
import com.bi.export.elasticsearch.processor._


/**
 * In Memory venue Storage.
 */
object VenueMeta {
  var venues = new collection.mutable.HashMap[String, VenueInfo]
  var chains = new collection.mutable.HashMap[String, ChainInfo]

  def getVenueInfoById(venueId: String): VenueInfo = {
    val maybeInfo: Option[VenueInfo] = venues.get(venueId)

    val result = maybeInfo match {
      case Some(x) => x
      case None => {
        val info: VenueInfo = AdminInfoService().getVenueInfo(venueId)
        venues.put(venueId, info)
        info
      }
    }
    result
  }


  def getChainInfoById(venudeId: String): ChainInfo = {
    val checkInfo: Option[ChainInfo] = chains.get(venudeId)

    val result = checkInfo match {
      case Some(x) => x
      case None => {
        val info: ChainInfo = AdminInfoService().getChainInfo(venudeId)
        info.name match {
          case Some(name) => {
            if (info.venueIds.isDefined) {
              val venues: Seq[String] = info.venueIds.get
              venues.foreach(venue => {
                chains.put(venue, info.copy(venueIds = None))
              })
            } else {
              chains.put(venudeId, info)
            }
            info
          }
          case None => {
            val undefChain: ChainInfo = ChainInfo(Some("0"), Some("undefined"), None, None)
            chains.put(venudeId, undefChain)
            undefChain
          }
        }
      }
    }
    result
  }
}
