var elasticsearch = require('elasticsearch');
var jmxproxybeat_indexpattern = require('./file/index-pattern.json');
var jmxproxybeat_mapping_visualization = require('./file/mapping/visualization.json');
var jmxproxybeat_visualization_bulk = require('./file/visualization.json');
var jmxproxybeat_mapping_dashboard = require('./file/mapping/dashboard.json');
var jmxproxybeat_dashboard_bulk = require('./file/dashboard.json');

var client = new elasticsearch.Client({
		host : 'cuplatform_dnsdock_1.dnsdock.cloud.unit:9200',
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
				//Check if defaultIndex field exist or if the field is not null
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

	}, 3000)
