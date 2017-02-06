FROM golang:1.7

COPY . /go/src/app
WORKDIR /go/src/app
RUN go get -d -v \
    && go install -v
