package main

import (
    "context"
    "fmt"
    //"strings"
    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func connectbridgenetwork() {
    //var poller_id,
    //var network_bridge_id string

    cli, err := client.NewEnvClient()
    if err != nil {
        panic(err)
    }

    networks, err := cli.NetworkList(context.Background(), types.NetworkListOptions{})
    if err != nil {
        panic(err)
    }
    fmt.Println(networks)

  /*  for _, network := range networks {
        //fmt.Println(networks.Name)
        if network.Name == "bridge" {
            network_bridge_id = network.ID
        }
    }

    network_bridge, err := cli.NetworkInspect(context.Background(), network_bridge_id)

    network_container := network_bridge.Containers

    for _, container_on_bridge := range network_container {
      //if strings.Contains(container.Names[0], "cuelk_*-polling*") {
        fmt.Println(container_on_bridge.Name)
      //}
    }

    containers, err := cli.ContainerList(context.Background(), types.ContainerListOptions{})
    if err != nil {
        panic(err)
    }

    fmt.Println(containers)
    /*for _, container := range containers {
        //fmt.Printf("%s %s\n", container.ID[:10], container.Image)
        if strings.Contains(container.Names[0], "_poller_") {
            poller_id = container.ID[:10]
            err = cli.NetworkConnect(context.Background(), network_bridge_id, poller_id, nil)
            fmt.Println("Attach bridge interface to poller container")
            if err != nil {
                panic(err)
            }
        } else {
          fmt.Println("bridge interface already attached to poller container")
        }
    }*/
}

func main() {
  connectbridgenetwork()
}
