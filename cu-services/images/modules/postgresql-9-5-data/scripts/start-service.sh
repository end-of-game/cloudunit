#!/bin/sh

# Callback bound to the application stop
terminate_handler() {
  exit 0;
}

trap 'terminate_handler' SIGTERM

# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done

