#!/bin/bash

java -jar cloudunitmonitor.jar -XX:MaxPermSize=128m -XX:+UseParNewGC -XX:MaxNewSize=256m -Xms768m -Xmx768m 
