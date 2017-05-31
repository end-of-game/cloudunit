FROM debian:8.7

MAINTAINER Nicolas MULLER <n.muller@treeptik.fr>
MAINTAINER William Bartlett <w.bartlett@treeptik.fr>

ENV DEBIAN_FRONTEND noninteractive
ENV COMPOSE_VERSION 1.13.0

RUN apt-get update -q \
	&& apt-get install -y -q --no-install-recommends curl ca-certificates \
	&& curl -o /usr/local/bin/docker-compose -L \
		"https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-Linux-x86_64" \
	&& chmod +x /usr/local/bin/docker-compose

# This container is a chrooted docker-compose
WORKDIR /app

COPY .env .
COPY docker-compose.yml .
COPY docker-entrypoint.sh /bin

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["start"]
