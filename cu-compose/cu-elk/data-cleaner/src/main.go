package main

import (
	"fmt"
	"strings"
	"os"
	"io/ioutil"
	"io"
	"net/http"
	"golang.org/x/net/context"
  "github.com/docker/docker/client"
  "github.com/docker/docker/api/types"
  "github.com/docker/docker/api/types/filters"
	"github.com/docker/docker/api/types/events"
)

func checkes(ELASTICSEARCH_URL string) {
	_, err := http.Get(ELASTICSEARCH_URL+"/_cluster/health")
	if err != nil {
		fmt.Println("Cannot connect to elasticsearch")
		os.Exit(1)
	}
	fmt.Println("Successfully connected to elasticsearch")
}

func purgeindex() {

}

func deletedoc(container_name string, ELASTICSEARCH_URL string) {
	body := strings.NewReader(`
	{
		"query": {
			"bool" : {
				"should" : [
					{ "term" : { "docker.container.name" : "`+container_name+`" } },
					{ "term" : { "beat.name" : "`+container_name+`" } },
					{ "term" : { "container_name" : "`+container_name+`" } },
					{ "term" : { "host" : "`+container_name+`" } }
				],
				"minimum_should_match" : 1,
				"boost" : 1.0
			}
		}
	}`)

	req, _ := http.NewRequest("POST", ELASTICSEARCH_URL+"/metricbeat-*,logstash-*,jmxtrans-*/_delete_by_query", body)
	req.Header.Set("Content-Type", "application/json")
	resp, err := http.DefaultClient.Do(req)
	if err != nil {
			panic(err)
	}
	defer resp.Body.Close()

	htmlData, err := ioutil.ReadAll(resp.Body)

	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	fmt.Println(container_name, string(htmlData))
}

func main() {
  client, err := client.NewClient("unix:///var/run/docker.sock", "1.25", nil, nil)
  if err != nil {
		panic(err)
  } else {
		fmt.Println("Successfully connected to docker socket")
	}

	var ELASTICSEARCH_URL string
	if os.Getenv("ELASTICSEARCH_URL") == "" {
		ELASTICSEARCH_URL = "http://elasticsearch:9200"
	} else {
		ELASTICSEARCH_URL = os.Getenv("ELASTICSEARCH_URL")
	}

	checkes(ELASTICSEARCH_URL)

  filters := filters.NewArgs()
  filters.Add("type", events.ContainerEventType)
  filters.Add("event", "destroy")
	expectedFiltersJSON := fmt.Sprintf(`{"type":{"%s":true}}`, events.ContainerEventType)

  options_filter := make(map[string]string)
  options_filter["filter"] = expectedFiltersJSON

  options := types.EventsOptions{
    Filters: filters,
  }

  messages, errs := client.Events(context.Background(), options)

  loop:
    for {
      select {
	      case err := <-errs:
	        if err != nil && err != io.EOF {
	          fmt.Println(err)
	        }
	        break loop
	      case e := <-messages:
					//fmt.Printf("\n%s", e.Actor.Attributes["name"])
					container_name := e.Actor.Attributes["name"]
					deletedoc(container_name, ELASTICSEARCH_URL)
			}
    }
}
