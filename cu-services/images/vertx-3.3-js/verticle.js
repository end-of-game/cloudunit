vertx.createHttpServer().requestHandler(function (request) {
    request.response().end("Hello world");
}).listen(8080);