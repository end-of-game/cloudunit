var express = require('express');
var app = express();

app.get('/', function (req, res) {
    res.send('Hello World!\nThis is a nodejs webapp written with expressjs');
});

app.listen(80, function () {
});