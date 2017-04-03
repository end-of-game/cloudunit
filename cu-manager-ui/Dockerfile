FROM node:5

WORKDIR /usr/apps/cu-manager-ui

RUN npm install -g grunt grunt-cli bower

COPY docker-entrypoint.sh /usr/local/bin/

EXPOSE 9000 35729
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]

