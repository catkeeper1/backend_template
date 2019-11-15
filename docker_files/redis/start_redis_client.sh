docker run -it --network backend_template_network --rm redis:5.0.6 redis-cli -h backend_template_session_server -p 6379

docker exec -it backend_template_session_server redis-cli