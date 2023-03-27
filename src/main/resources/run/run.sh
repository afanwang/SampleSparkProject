#!/bin/bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do 
  SCRIPT_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$SCRIPT_DIR/$SOURCE" 
done
SCRIPT_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

echo $SCRIPT_DIR

CONFIG_FILE="$SCRIPT_DIR/environment.conf"

USAGE="Usage: $(tput bold)$0 [-d DATE] [-e <environment_config>] $(tput sgr0)
              $(tput bold)-d <DATE>           $(tput sgr0)  Date for indexing
              $(tput bold)-e <config.conf>    $(tput sgr0)  Configuration file name full path (default = $CONFIG_FILE)"

while getopts d:e: OPT; do
  case $OPT in
    d)  DATE="-d $OPTARG"
        ;;
    e)  CONFIG_FILE="$OPTARG"
        ;;
    ?) echo "$USAGE"
        exit 1
  esac
done

if [ -f "$CONFIG_FILE" ]; then
    ENV_CONF=$(cat $CONFIG_FILE)
    SPARK_PATH=$(echo "$ENV_CONF" | sed -n 's/sparkPath=\(.*\)/\1/gp')
    SPARK_MASTER_URL=$(echo "$ENV_CONF" | sed -n 's/sparkMasterUrl=\(.*\)/\1/gp')
    ELASTIC_INDEX=$(echo "$ENV_CONF" | sed -n 's/indexName=\(.*\)/\1/gp')
    ELASTIC_HOST=$(echo "$ENV_CONF" | sed -n 's/elasticHost=\(.*\)/\1/gp')
    APPLICATION_JAR=$(echo "$ENV_CONF" | sed -n 's/appJarName=\(.*\)/\1/gp')
    MAIN_CLASS=$(echo "$ENV_CONF" | sed -n 's/mainClass=\(.*\)/\1/gp')
    COULMN_FAMILY=$(echo "$ENV_CONF" | sed -n 's/columnfamily=\(.*\)/\1/gp')
else
    echo "environment configuration missed by path $CONFIG_FILE"
    exit 0;
fi
echo "Spark path: $SPARK_PATH"
echo "Spark Master URL: $SPARK_MASTER_URL"
echo "Elasticsearch host:$ELASTIC_HOST. Index: $ELASTIC_INDEX."
echo "Application JAR: $APPLICATION_JAR"
echo "Main class: $MAIN_CLASS"

curl -XDELETE "http://$ELASTIC_HOST:9200/$ELASTIC_INDEX/"

#Before start spark/sbin/start-all.sh
cmd="$SPARK_PATH/bin/spark-submit --master $SPARK_MASTER_URL --driver-memory 4g --class $MAIN_CLASS \
$SCRIPT_DIR/../jars/$APPLICATION_JAR \
-f $COULMN_FAMILY -i $ELASTIC_INDEX $DATE"

echo $cmd
eval $cmd

