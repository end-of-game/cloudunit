export REGISTRY_STORAGE_DELETE_ENABLED=true
docker rm -f registry
rm -f ./data
docker run -d -p 5000:5000 -p 5001:5001 --restart=always --name registry -e REGISTRY_STORAGE_DELETE_ENABLED=true -v `pwd`/data:/var/lib/registry -v `pwd`/config.yml:/etc/docker/registry/config.yml registry:2

