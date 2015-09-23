#!/usr/bin/expect -f
#Ne reconnait pas les variables d'environnement

set CU_PASSWORD [lindex $argv 0]


spawn /cloudunit/scripts/ssh-copy-id.sh

expect "Are you sure you want to continue connecting (yes/no)?"
send -- "yes\r"

expect "'s password:"
send -- "$CU_PASSWORD\r"

interact
