FROM mysql:5.6.35

ENV MYSQL_RANDOM_ROOT_PASSWORD "true"

LABEL io.cloudunit="" \
      io.cloudunit.type=module \
      io.cloudunit.name.service=mysql \
      io.cloudunit.name.display="MySQL $MYSQL_MAJOR" \
      io.cloudunit.version=$MYSQL_MAJOR \
      io.cloudunit.variables="{\"MYSQL_USER\": \"USER\", \"MYSQL_PASSWORD\": \"PASSWORD\", \"MYSQL_DATABASE\": \"NAME\"}"

