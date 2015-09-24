#!/usr/bin/python

#
# LICENSE: MIT
#
# Copyright (C) 2014 Samuel Stauffer
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to
# deal in the Software without restriction, including without limitation
# the rights to use, copy, modify, merge, publish, distribute, sublicense,
# and/or sell copies of the Software, and to permit persons to whom the
# Software is furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

import socket
import sys
from optparse import OptionParser

EXIT_OK = 0
EXIT_WARN = 1
EXIT_CRITICAL = 2

def get_info(host, port, timeout):
    socket.setdefaulttimeout(timeout or None)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))
    s.send("*1\r\n$4\r\ninfo\r\n")
    buf = ""
    while '\r\n\r\n' not in buf:
        buf += s.recv(1024)
    s.close()
    return dict(x.split(':', 1) for x in buf.split('\r\n') if ':' in x)

def build_parser():
    parser = OptionParser()
    parser.add_option("-s", "--server", dest="server", help="Redis server to connect to.", default="127.0.0.1")
    parser.add_option("-p", "--port", dest="port", help="Redis port to connect to.", type="int", default=6379)
    parser.add_option("-w", "--warn", dest="warn_memory", help="Memory utilization (in MB) that triggers a warning status.", type="int")
    parser.add_option("-c", "--critical", dest="crit_memory", help="Memory utilization (in MB) that triggers a critical status.", type="int")
    parser.add_option("-t", "--timeout", dest="timeout", help="Number of milliesconds to wait before timing out and considering redis down", type="int", default=2000)
    return parser

def main():
    parser = build_parser()
    options, _args = parser.parse_args()
    if not options.warn_memory:
        parser.error("Warning level required")
    if not options.crit_memory:
        parser.error("Critical level required")

    try:
        info = get_info(options.server, int(options.port), timeout=options.timeout / 1000.0)
    except socket.error, exc:
        print "CRITICAL: Error connecting or getting INFO from redis %s:%s: %s" % (options.server, options.port, exc)
        sys.exit(EXIT_CRITICAL)

    memory = int(info.get("used_memory_rss") or info["used_memory"]) / (1024*1024)
    if memory > options.crit_memory:
        print "CRITICAL: Redis memory usage is %dMB (threshold %dMB)" % (memory, options.crit_memory)
        sys.exit(EXIT_CRITICAL)
    elif memory > options.warn_memory:
        print "WARN: Redis memory usage is %dMB (threshold %dMB)" % (memory, options.warn_memory)
        sys.exit(EXIT_WARN)

    print "OK: Redis memory usage is %dMB" % memory
    sys.exit(EXIT_OK)

if __name__ == "__main__":
    main()
