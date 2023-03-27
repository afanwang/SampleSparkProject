package com.bi.persist

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._


import com.bi.events._
import com.bi.migration.config._
import com.bi.util._
import org.apache.hadoop.conf._
import org.apache.hadoop.mapreduce._
import org.apache.spark.rdd._
import org.elasticsearch.hadoop.mr._
import org.json4s._
import org.json4s.ext._
import org.json4s.native.Serialization._
import org.json4s.native._

import scala.reflect._

import org.elasticsearch.spark._

/**
 * Data Persistence layer.
 */
class RddSaverHadoop[I <: VenueEvent : ClassTag](rdd: RDD[I], conf: BaseConfig) extends Serializable {

  def save() {
    conf match {
      case cfg: CassandraSourceConfig => {
      //  rdd.saveToCassandra(conf.keyspace, conf.columnfamily.get)(CassandraConnector(targetConf), RowWriterFactory)
      }
      case cfg: ElasticSearchConfig => {
        val index: String = cfg.indexName
        val jsonRdd = rdd.map(x => {
          implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

          val payloadForMerge = JsonMethods.parse(write(x.payload))
          val deviceJsonForMerge = JsonMethods.parse(write(x))

          val result = write(deviceJsonForMerge.removeField(x => {
            x._1 == "payload"
          }) merge payloadForMerge)
          result
        })

        val options = Map("es.nodes" -> cfg.host, "es.port" -> cfg.port).filter(x => x._2.isDefined).map(param => (param._1, param._2.get))

        jsonRdd.saveJsonToEs(s"${cfg.indexName}/${cfg.indexType}", options)


      }
      case _ => throw new IllegalArgumentException(s"Specified processor ${conf.sourceType} do not supported")
    }
  }

}

object RddSaverHadoop {
  def apply[I <: VenueEvent : ClassTag](rdd: RDD[I], conf: BaseConfig) = {
    new RddSaverHadoop[I](rdd, conf)
  }
}
