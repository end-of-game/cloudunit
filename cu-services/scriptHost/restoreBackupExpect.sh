#!/usr/bin/expect -f

set containerApplicatif [lindex $argv 0]
set cuDbUser [lindex $argv 1]
set cuDbPassword [lindex $argv 2]
set cuApplicationName [lindex $argv 3]
set cuPassword [lindex $argv 4]

spawn /vagrant_cloudunit/cu-services/scriptHost/restoreBackup.sh $containerApplicatif $cuDbUser $cuDbPassword $cuApplicationName $cuPassword

expect "Are you sure you want to continue connecting (yes/no)?"
send -- "yes\r"
expect "'s password:"
send -- "root\r"
interact
