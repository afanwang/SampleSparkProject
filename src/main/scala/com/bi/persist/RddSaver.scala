package com.bi.persist

import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._
import com.datastax.spark.connector.writer._
import com.bi.events._
import com.bi.migration.config._
import com.bi.util._
import org.apache.spark.rdd._
import org.elasticsearch.action.bulk._
import org.elasticsearch.client.transport._
import org.elasticsearch.common.transport._
import org.elasticsearch.indices._
import org.json4s._
import org.json4s.ext._
import org.json4s.native.Serialization.write
import org.json4s.native._

import scala.reflect._

/**
 * Data Persistence layer.
 */
class RddSaver[+I <: VenueEvent : ClassTag](rdd: RDD[I], conf: CassandraSourceConfig) {

  lazy val targetConf = BiConfigUtils.casandraSparkConfig(conf)

  val INDEX: String = conf.keyspace

  def save() {
    conf.sourceType match {
      case "Cassandra" => {
      //  rdd.saveToCassandra(conf.keyspace, conf.columnfamily.get)(CassandraConnector(targetConf), RowWriterFactory)
      }
      case "elasticsearch" => {
  /*      val client = new TransportClient()
        client.addTransportAddress(new InetSocketTransportAddress(conf.host, 9300))
        val bulkRequest: BulkRequestBuilder = client.prepareBulk


        try {
          client.admin().indices().prepareDelete(INDEX).execute().actionGet(5000)

        } catch {
          case imx: IndexMissingException => println(imx)
        }

        client.admin().indices().prepareCreate(INDEX).execute().actionGet()

        val source = scala.io.Source.fromURL(getClass.getResource("/device_stats_es_mapping.json")).mkString
        client.admin().indices().preparePutMapping(INDEX).setType("device").setSource(source).execute().actionGet()
        val packages: Iterator[Array[I]] = rdd.collect().sliding(3000)
        packages.foreach(rawPack => {
          val bulkRequest = client.prepareBulk()

          rawPack.foreach(x => {
            implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

            val payloadForMerge = JsonMethods.parse(write(x.payload))
            val deviceJsonForMerge = JsonMethods.parse(write(x))

            val result = write(deviceJsonForMerge.removeField(x => {
              x._1 == "payload"
            }) merge payloadForMerge)

            bulkRequest.add(client.prepareIndex(INDEX, "device").setSource(result))

          })
          bulkRequest.execute().actionGet()
        })

        client.close()*/
      }
      case _ => throw new IllegalArgumentException(s"Specified processor ${conf.sourceType} do not supported")
    }
  }

}

object RddSaver {
  def apply[I <: VenueEvent : ClassTag](rdd: RDD[I], conf: CassandraSourceConfig) = {
    new RddSaver[I](rdd, conf)
  }
}
