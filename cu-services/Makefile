colon := :
$(colon) := :

.DEFAULT: all

SERVICES := base nginx apache tomcat fatjar wildfly postgre postgis mongo mysql activemq rabbitmq elastic redis vertx
base_VERSIONS := jessie 12.04 14.04 16.04
nginx_VERSIONS := 1.10
apache_VERSIONS := 2.2
tomcat_VERSIONS := 6 7 8 8.5 9
fatjar_VERSIONS := 1
wildfly_VERSIONS := 8 9 10
postgre_VERSIONS := 9.3 9.4 9.5
postgis_VERSIONS := 2.2
mongo_VERSIONS := 2.6
mysql_VERSIONS := 5.5 5.6 5.7
activemq_VERSIONS := 5.13
rabbitmq_VERSIONS := 3.6
elastic_VERSIONS := 2.4
redis_VERSIONS := 3.2
vertx_VERSIONS := 3.3-ruby 3.3-js 3.3-groovy 3.3-java

DOCKER_PREFIX := cloudunit
DOCKERFLAGS := --no-cache --rm

IMAGEPATH := images

.PHONY: all clean check

all: $(SERVICES)

define IMAGE_TEMPLATE
$(1)_NAME := $$(DOCKER_PREFIX)/$$(subst _,$:,$(1))
ifndef PULL
$(1): $$(IMAGEPATH)/$$(subst _,-,$(1))
	docker build $$(DOCKERFLAGS) -t $$($(1)_NAME) $$<
else
$(1):
	docker pull $$($(1)_NAME)
endif

$(1)-check:
	@bash -c '[[ ! -z `docker images --format "{{.ID}}" $$($(1)_NAME)` ]] || echo "Missing image $$($(1)_NAME)"'

check: $(1)-check

$(1)-clean:
	docker rmi -f $(1)_NAME

clean: $(1)-clean

.PHONY: $(1) $(1)-check $(1)-clean
endef

define SERVICE_TEMPLATE
ALL_IMAGES += $$($(1)_IMAGES)
$(1)_IMAGES := $$(patsubst %,$(1)_%,$$($(1)_VERSIONS))

$(1): $$($(1)_IMAGES)

.PHONY: $(1)
endef

$(foreach service,$(SERVICES),$(eval $(call SERVICE_TEMPLATE,$(service))))

$(foreach image,$(ALL_IMAGES),$(eval $(call IMAGE_TEMPLATE,$(image))))
