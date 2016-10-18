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
                ./start.sh
                ;;

        [nN] | [n|N][O|o] )
                echo "Not agreed, you can't proceed the installation";
                exit 1
                ;;
        *) echo "Invalid input"
                exit 1
            ;;
esac

## ALWAYS RIGHT
exit 0

