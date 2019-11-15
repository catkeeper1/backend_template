#!/bin/bash

docker run -d --network backend_template_network --name backend_template_mongodb \
    -e MONGO_INITDB_ROOT_USERNAME=admin \
    -e MONGO_INITDB_ROOT_PASSWORD=admin \
    -e MONGO_INITDB_DATABASE=backend_template  \
    -v `pwd`/`dirname "$0"`/config:/etc/mongo  \
    -v ~/docker_data/mongodb/data/:/data/db   \
    -v ~/docker_data/mongodb/log/:/data/log   \
    -p "27017:27017" \
    mongo:4.2.1 \
    --config /etc/mongo/mongod.conf \
    --replSet rs0
