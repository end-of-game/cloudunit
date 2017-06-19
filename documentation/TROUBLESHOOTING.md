# How to restart Cloudunit

## How to restart the production environment without reseting data

```
~/cloudunit/cu-compose/cu-docker-compose.sh with-elk
```

## How to reset the production environment 

Persistent volumes won't be deleted. 

```
~/cloudunit/cu-compose/cu-docker-compose.sh reset
```

# Docker panic

Log in as *root*
```
service docker restart
```

Log in as *admincu*
```
~/cloudunit/cu-compose/cu-docker-compose.sh with-elk
```

# Bind for 0.0.0.0:xxx failed: port is already allocated

This problem occurs when docker has lost synchronization.
This issue is common for many versions : https://github.com/moby/moby/issues/20486

Log in as *root*
```
service docker stop
rm /var/lib/docker/network/files/local-kv.db
service docker start
```

Log in as *admincu*
```
~/cloudunit/cu-compose/cu-docker-compose.sh with-elk
```

# Kibana dasboard error reduce_search_phase_exception

This problem occurs when reponse time of elasticsearch query is to slow, usually it come from the indexes of elasticsearch that have not been purged.

Play this command line on the host where :
${elasticsearch-container-ip} = elasticsearch container ip given by "docker inspect cu-elasticsearch"
${month} and ${day} = index are daily dated, in order to purge elasticsearch insert old index or list indexes with curl '${elasticsearch-container-ip}:9200/_cat/indices?v'
```
curl -X DELETE '${elasticsearch-container-ip}:9200/metricbeat-2017.${month}.${day}'
curl -X DELETE '${elasticsearch-container-ip}:9200/jmxtrans-2017.${month}.${day}'
```


