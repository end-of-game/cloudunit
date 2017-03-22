#!/bin/sh

python /app/init.py
exec /usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf
