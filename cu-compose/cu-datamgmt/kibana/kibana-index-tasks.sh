#! /bin/bash

action=$1


if [ "$action" == "-restore" ]; then
  elasticdump --input=file/kibana-index-mapping.json --output=http://elasticsearch:9200/.kibana --type=mapping
	elasticdump --input=file/kibana-index.json --output=http://elasticsearch:9200/.kibana --type=data
elif [ "$action" == "-backup" ];then
  rm -f file/kibana-index-mapping.json
  rm -f file/kibana-index.json
  elasticdump --input=http://elasticsearch:9200/.kibana --output=file/kibana-index-mapping.json --type=mapping
	elasticdump --input=http://elasticsearch:9200/.kibana --output=file/kibana-index.json --type=data
else
  echo "Simple Backup/Restore kibana index in order to persist doashboard/visualization made on kibana"
  echo "Usage :"
  echo "./kibana-index-tasks.sh -restore     ##  Will restore index from file in elasticsearch database"
  echo "./kibana-index-tasks.sh -backup      ##  Will backup all modification into file for next deployment"
fi

echo $1
