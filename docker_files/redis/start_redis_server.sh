#!/bin/bash



docker run -v `pwd`/`dirname "$0"`/redis_session.conf:/usr/local/etc/redis/redis.conf \
           --net=backend_template_network -p "6379:6379" -d \
           --name backend_template_session_server redis:5.0.6 \
           redis-server /usr/local/etc/redis/redis.conf


docker run -v `pwd`/`dirname "$0"`/redis_cache.conf:/usr/local/etc/redis/redis.conf \
           --net=backend_template_network -p "6380:6379" -d \
           --name backend_template_cache_server redis:5.0.6 \
           redis-server /usr/local/etc/redis/redis.conf