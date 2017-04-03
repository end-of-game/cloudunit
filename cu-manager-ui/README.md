# BUILD

`docker build -t cloudunit/cu-manager-ui .`

# RUN

`docker run --net=host -p 9000:9000 -p 35729:35729 -v $(pwd):/usr/apps/cu-manager-ui cloudunit/cu-manager-ui`

