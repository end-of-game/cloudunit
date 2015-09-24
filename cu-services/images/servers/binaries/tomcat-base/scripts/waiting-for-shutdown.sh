#!/bin/bash
SHUTDOWN_WAIT=$2  
pid=`pidof $1`
if [ -n "$pid" ]
then
    let kwait=$SHUTDOWN_WAIT 
    count=0;
    until [ `ps -p $pid | grep -c $pid` = '0' ] || [ $count -gt $kwait ]
    do
        echo -n -e "\nwaiting for processes to exit";
        sleep 1
        let count=$count+1;
    done

    if [ $count -gt $kwait ]; then
        echo -n -e "\nkilling processes which didn't stop after $SHUTDOWN_WAIT seconds"
        kill -9 $pid
    fi
    
    echo "Server has stopped"
else
    echo "Server is not running"
fi
