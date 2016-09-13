var elasticsearch = require('elasticsearch');
var fs = require("fs");
var jmxproxybeat_indexpattern = require('./file/index-pattern.json');
var jmxproxybeat_mapping_visualization = require('./file/mapping/visualization.json');
var jmxproxybeat_visualization_bulk = require('./file/visualization.json');
var jmxproxybeat_mapping_dashboard = require('./file/mapping/dashboard.json');
var jmxproxybeat_dashboard_bulk = require('./file/dashboard.json');

var client = new elasticsearch.Client({
		host : '192.168.50.4:9200',
		log : 'warning'
	});

function inites(callback) {
	client.indices.exists({
		index : '.kibana'
	}, function (error, exists) {
		if (exists === true) {
			client.get({
				index : '.kibana',
				type : 'config',
				id : '5.0.0-alpha5'
			}, function (error, response) {
				if (typeof response._source.defaultIndex == 'undefined' || response._source.defaultIndex == null) {
					//If defaultIndex is not set check if jmxproxybeat index-patter exists
					console.log("Set default Index because not configured")
					client.exists({
						index : '.kibana',
						type : 'index-pattern',
						id : 'jmxproxybeat-*'
					}, function (error, exists) {
						// if pattern does not exists create it
						if (exists === false) {
							client.create({
								index : '.kibana',
								type : 'index-pattern',
								id : 'jmxproxybeat-*',
								body : jmxproxybeat_indexpattern
							}, function (error, response) {})
						}
					})
					// Update defaultIndex fied to jmxproxybeat
					client.update({
						index : '.kibana',
						type : 'config',
						id : '5.0.0-alpha5',
						body : {
							doc : {
								defaultIndex : 'jmxproxybeat-*'
							}
						}
					}, function (error, response) {
						callback(true)
					})
				} else {
					//console.log("Kibana default pattern already exist")
					//console.log("Kibana UP and Running")
					callback(true)
				}
			})
		} else {
			//console.log("Kibana index doesn't exist")
			callback(false)
		}
	})
}

if (process.argv.length == 2) {
var initesforkibana = setInterval(function () {
		inites(function (result) {
			if (result) {
				client.indices.putMapping({
					updateAllType : false,
					index : '.kibana',
					type : 'visualization',
					body : jmxproxybeat_mapping_visualization
				}, function (error, response) {
					client.indices.putMapping({
						updateAllType : false,
						index : '.kibana',
						type : 'dashboard',
						body : jmxproxybeat_mapping_dashboard
					}, function (error, response) {
						client.bulk({
							body : jmxproxybeat_visualization_bulk
						}, function (error, result) {
							client.bulk({
								body : jmxproxybeat_dashboard_bulk
							}, function (error, result) {
								clearInterval(initesforkibana)
							})
						})
					})
				})
				console.log("Tomcat dashboard loaded, Kibana UP and Running")
			}
		})

	}, 10000)
} else {
	container_name = process.argv[2]
	var jmxproxybeat_visualization_bulk = fs.readFileSync("file/visualization.json").toString();
	var jmxproxybeat_dashboard_bulk = fs.readFileSync("file/dashboard.json").toString();
	jmxproxybeat_visualization_bulk = jmxproxybeat_visualization_bulk.replace(/sethostname/gi, container_name)
	jmxproxybeat_visualization_bulk = jmxproxybeat_visualization_bulk.replace(/Tomcat-Thread/gi, "Tomcat-Thread-"+container_name)	
	jmxproxybeat_visualization_bulk = jmxproxybeat_visualization_bulk.replace(/Tomcat-Load/gi, "Tomcat-Load-"+container_name)	
	jmxproxybeat_visualization_bulk = jmxproxybeat_visualization_bulk.replace(/Tomcat-Class/gi, "Tomcat-Class-"+container_name)	
	jmxproxybeat_visualization_bulk = jmxproxybeat_visualization_bulk.replace(/Tomcat-Memory/gi, "Tomcat-Memory-"+container_name)	
   jmxproxybeat_dashboard_bulk = jmxproxybeat_dashboard_bulk.replace(/Tomcat-Thread/gi, "Tomcat-Thread-"+container_name)	
   jmxproxybeat_dashboard_bulk = jmxproxybeat_dashboard_bulk.replace(/Tomcat-Load/gi, "Tomcat-Load-"+container_name)	
   jmxproxybeat_dashboard_bulk = jmxproxybeat_dashboard_bulk.replace(/Tomcat-Class/gi, "Tomcat-Class-"+container_name)
   jmxproxybeat_dashboard_bulk = jmxproxybeat_dashboard_bulk.replace(/Tomcat-Memory/gi, "Tomcat-Memory-"+container_name)	
   jmxproxybeat_dashboard_bulk = jmxproxybeat_dashboard_bulk.replace(/Tomcat-Exemple/gi, "Tomcat-"+container_name)
   fs.appendFileSync('visualization_temp.json', jmxproxybeat_visualization_bulk, 'utf8');
   fs.appendFileSync('dashboard_temp.json', jmxproxybeat_dashboard_bulk, 'utf8');
   jmxproxybeat_visualization_bulk = require('./visualization_temp.json');
   jmxproxybeat_dashboard_bulk = require('./dashboard_temp.json');
   client.bulk({
     body : jmxproxybeat_visualization_bulk
	}, function (error, result) {
	  if (!error) { console.log("Visualization imported without errors") }	  
	  client.bulk({
	  	 body : jmxproxybeat_dashboard_bulk
	  }, function (error, result) {
	    if (!error) { console.log("Dashboard imported without errors") }	  
	  })
   })
   fs.unlinkSync('visualization_temp.json')
   fs.unlinkSync('dashboard_temp.json')
}