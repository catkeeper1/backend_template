docker run -it --network backend_template_network --rm mongo:4.2.1 mongo \
       --host backend_template_mongodb --username admin --password admin \
       --authenticationDatabase admin backend_template


#       run "rs.initiate()" after enter the cli.
