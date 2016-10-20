#!/bin/bash

function reset {
    docker rm -vf $(docker ps -aq)
    docker volume rm $(docker volume ls -q)
    docker rmi -f $(docker images -q)
}

echo -n "Do you agree with this? [yes or no]: "
read yno
case $yno in

        [yY] | [yY][Ee][Ss] )
                echo "Agreed"
                reset
                ;;

        [nN] | [n|N][O|o] )
                echo "Not agreed, you can't reset the platform";
                exit 1
                ;;
        *) echo "Invalid input"
                exit 1
            ;;
esac


