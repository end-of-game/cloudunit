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

  val httpConf = http
    .baseURL(cuHost)
    .acceptHeader("application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .header(HttpHeaderNames.Accept, HttpHeaderValues.ApplicationJson)
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")



	val headers_215 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Content-Type" -> "application/json;charset=utf-8")


  val scn = scenario("DeployApplication")
        .exec(http("request_1")
			.post("/user/authentication")
			.formParam("j_username", "johndoe")
			.formParam("j_password", "abc2015"))
			.pause(1)
		
		.exec(http("request_2")
			.post("/application")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling", "serverName": "tomcat-8" }""")).asJSON)
			.pause(3)

			.exec(http("request_3")
			.post("/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v1" }""")).asJSON)
			.pause(3)
		
			.exec(http("request_4")
			.post("/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v2" }""")).asJSON)
			.pause(3)
		
			.exec(http("request_5")
			.post("/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v3" }""")).asJSON)
			.pause(3)
		
			.exec(http("request_6")
			.post("/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "name": "gatling-v4" }""")).asJSON)
			.pause(3)

			.exec(http("request_7")
			.post("/module")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "imageName": "postgresql-9-3", "applicationName": "gatling" }""")).asJSON)
			.pause(3)
		

			.exec(http("request_8")
			.put("/server/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt1" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v1","applicationName": "gatling" } """)).asJSON)
			.pause(3)
		
				.exec(http("request_9")
			.put("/server/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt2" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v2","applicationName": "gatling" } """)).asJSON)
			.pause(3)
		

				.exec(http("request_10")
			.put("/server/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt3" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v3","applicationName": "gatling" } """)).asJSON)
			.pause(3)
		

			.exec(http("request_11")
			.put("/server/volume")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ 	"containerName": "gatling-johndoe", "path": "/opt4" , "deferedRestart": "true" , "mode": "ro", "volumeName": "gatling-v4","applicationName": "gatling" } """)).asJSON)
			.pause(3)
			

			.exec(http("request_12")
			.put("/server/configuration/jvm")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling" ,"jvmOptions": "-Dgatlingtest=true","jvmMemory": "512"} """)).asJSON)
			.pause(20)
		
			.exec(http("request_13")
			.post("/application/stop")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling"} """)).asJSON)
			.pause(5)


			.exec(http("request_14")
			.post("/application/start")
			.header("Content-Type", "application/json")
			.body(StringBody("""{ "applicationName": "gatling"} """)).asJSON)
			.pause(5)

			.exec(http("request_15")
			.post("/application/gatling/deploy")
			.header("Content-Type", "application/json")
			.body(StringBody("""{"deployUrl":"https://github.com/Treeptik/cloudunit/releases/download/1.0/pizzashop-postgres.war"} """)).asJSON)
			.pause(15)

			.exec(http("request_16")
			.get("http://gatling-johndoe.192.168.50.4.xip.io/pizzashop-postgres/"))
			.pause(10)

			.exec(http("request_17")
			.delete("/application/gatling"))
			.pause(2)




  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}
