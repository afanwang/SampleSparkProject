package com.company.bi.export.elasticsearch

import com.company.bi.events._
import com.company.bi.export.elasticsearch.processor._
import com.company.bi.migration._
import com.company.bi.migration.config.{ElasticSearchConfig, ApplicationConfig}
import com.company.bi.persist._
import com.company.bi.util._
import com.typesafe.config.{Config, ConfigFactory}
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.indices.IndexMissingException
import scopt.OptionParser

/**
 * Starts ElasticSearch export process.
 *
 * Application input arguments.
 *
 * 1. Application config
 * 2. Date for which indexing will be performed.
 *
 * Example: -f "device_events" -i qa3-device-events2 -m "local[1]" -d "2014-09-29"
 *
 * If date was not listed full indexing performed
 */
object ExportProcessor extends App {

  private val procOpts: Option[ExportProcessorOptions] = parseArgs()

  if (procOpts.isDefined) {
    val options: ExportProcessorOptions = procOpts.get
    if (options.jsonConfPath != "") ApplicationConfig.init(options.jsonConfPath)
    val (source, target, opts) = BiConfigUtils.initAppConfig(options)

    //ElasticSearch index pre-configuration
    prepareIndexState(options, target.asInstanceOf[ElasticSearchConfig])

    //Data processing
    val (sc, rdd) = RddFactory.createContextRDD[DeviceEvent](source, opts)

    val rddOut = new DeviceEventsProcessorES().transform(rdd, opts)

    //Data persistence
    RddSaverHadoop[DeviceEventES](rddOut, target).save()

  }

  def parseArgs(): Option[ExportProcessorOptions] = {
    val parser = new OptionParser[ExportProcessorOptions]("ExportProcessor") {
      head("Export Processor from Cassandra to Elasticsearch")
      opt[String]('c', "config") action { (data, cfg) => cfg.copy(jsonConfPath = data)} text "Application config (default spark-app.conf)"
      opt[String]('p', "proc") action { (data, cfg) => cfg.copy(jsonConfPath = data)} text "Processing options"
      opt[String]('m', "master") action { (masterUrl, cfg) => cfg.copy(master = Some(masterUrl))} text "Spark master URL"
      opt[String]('d', "date") action { (targetDate, cfg) => cfg.copy(dateFrom = Some(targetDate), dateTo = Some(targetDate))} text "Indexing for the date"
      opt[String]('f', "columnfamily") required() action { (cf, cfg) => cfg.copy(columnFamily = cf)} text "Cassandra column family"
      opt[String]('i', "index") required() action { (idxType, cfg) => cfg.copy(index = idxType)} text "Elasticsearch Index Name"
    }
    val maybeOptions: Option[ExportProcessorOptions] = parser.parse(args, ExportProcessorOptions())
    maybeOptions
  }

  def prepareIndexState(options: ExportProcessorOptions, esConfig: ElasticSearchConfig): Unit = {
    val client = new TransportClient()
    client.addTransportAddress(new InetSocketTransportAddress(esConfig.host.getOrElse("localhost"), 9300))

    try {
      client.admin().indices().prepareDelete(options.index).execute().actionGet(5000)

    } catch {
      case imx: IndexMissingException => println(imx)
    }

    client.admin().indices().prepareCreate(options.index).execute().actionGet()

    val source = scala.io.Source.fromURL(getClass.getResource("/device_stats_es_mapping.json")).mkString
    val source_mod = s"{${options.index}:$source}"
    client.admin().indices().preparePutMapping(options.index).setType(options.index).setSource(source_mod).execute().actionGet()

    client.close()
  }


}

case class ExportProcessorOptions(jsonConfPath: String = "", master: Option[String] = None, columnFamily: String = "", index: String = "", dateFrom: Option[String] = None, dateTo: Option[String] = None)
