/**
 * Copyright 2011-2017 GatlingCorp (http://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloudunit

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CloudUnitSimulation extends Simulation {

  val cuHost = "http://localhost:8080"

  val cu_username = "johndoe"
  val cu_password = "abc2015"
  val applicationName = "gatling"// don't change this because there's still some parameter hard coded with "gatling"
  val repeatScenario = 100


  val url_authentification = "/user/authentication"
  val url_application = "/application"
  val url_volume = "/volume"
  val url_module = "/module"
  val url_mount_volume = "/server/volume"
  val url_jvm_options = "/server/configuration/jvm"
  val url_application_stop = "/application/stop"
  val url_application_start = "/application/start"
  val url_deploy_war = "/application/"+applicationName+"/deploy"
  var url_deployed_war = "http://"+applicationName+"-"+cu_username+".192.168.50.4.xip.io/pizzashop-postgres/"



  val httpConf = http
    .baseURL(cuHost)
    .acceptHeader("application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .header(HttpHeaderNames.Accept, HttpHeaderValues.ApplicationJson)
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


  val scn = scenario("DeployApplication")
  .repeat(repeatScenario){ 
        exec(http("request_1")
			.post(url_authentification)
			.formParam("j_username", cu_username)
			.formParam("j_password", cu_password))
			.pause(1)
		
		.exec(http("request_2")
			.post(url_application)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling", "serverName": "tomcat-8" }""")).asJSON)
			.pause(3)

			.exec(http("request_3")
			.post(url_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v1" }""")).asJSON)
			.pause(3)
		
			.exec(http("request_4")
			.post(url_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v2" }""")).asJSON)
			.pause(3)
		
			.exec(http("request_5")
			.post(url_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v3" }""")).asJSON)
			.pause(3)
		
			.exec(http("request_6")
			.post(url_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v4" }""")).asJSON)
			.pause(3)

			.exec(http("request_7")
			.post(url_module)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "imageName": "postgresql-9-3", "applicationName": "gatling" }""")).asJSON)
			.pause(3)
		

			.exec(http("request_8")
			.put(url_mount_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt1" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v1","applicationName": "gatling" } """)).asJSON)
			.pause(3)
		
				.exec(http("request_9")
			.put(url_mount_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt2" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v2","applicationName": "gatling" } """)).asJSON)
			.pause(3)
		

				.exec(http("request_10")
			.put(url_mount_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt3" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v3","applicationName": "gatling" } """)).asJSON)
			.pause(3)
		

			.exec(http("request_11")
			.put(url_mount_volume)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt4" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v4","applicationName": "gatling" } """)).asJSON)
			.pause(3)
			

			.exec(http("request_12")
			.put(url_jvm_options)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling" ,"jvmOptions": "-Dgatlingtest=true","jvmMemory": "512"} """)).asJSON)
			.pause(20)
		
			.exec(http("request_13")
			.post(url_application_stop)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling"} """)).asJSON)
			.pause(5)


			.exec(http("request_14")
			.post(url_application_start)
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling"} """)).asJSON)
			.pause(5)

			.exec(http("request_15")
			.post(url_deploy_war)
			.header("Content-Type", "application/json")
			.body(StringBody("""{"deployUrl":"https://github.com/Treeptik/cloudunit/releases/download/1.0/pizzashop-postgres.war"} """)).asJSON)
			.pause(15)

			.exec(http("request_16")
			.get(url_deployed_war))
			.pause(10)

			.exec(http("request_17")
			.delete("/application/gatling"))
			.pause(5,15)


  }

	  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))

}

