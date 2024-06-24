#!/bin/sh
set -e
mvn -B --quiet package
exec java -jar /tmp/codecrafters-redis-target/java_redis.jar --dir /tmp/redis-files --dbfilename dump.rdb rdb.rdb "$@"