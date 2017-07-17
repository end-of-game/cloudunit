/*
 Date: 22th June
 Author: IsmaStormZ
 */

const http = require('http');               // To use the HTTP server and client one must require('http')
const express = require('express');         // express is a minimalist framework for node
const bodyParser = require('body-parser');  // body-parser extracts entire body portion of an request stream and exposes it on req.body
const sendmail = require('sendmail')({      // Send mail without SMTP server
    logger: {
        debug: console.log,
        info: console.info,
        warn: console.warn,
        error: console.error
    }});

const app = express();
const port = Number(process.env.PORT || 3000); // set the environment variable PORT to tell your web server what port to listen on, whatever is in the environment variable PORT, or 3000 if there's nothing there.
const userMail = String(process.env.USER_MAIL);

//Parsed the text as JSON

app.use(bodyParser.json());

// Parses the text as URL encoded data

app.use(bodyParser.urlencoded({
    extended:true
}));

// express.static permit to use any static files(js, html, css) related to the static directory

app.use('/', express.static(__dirname));

// Home page

app.get('/', (req,res) => {
    res.send('Hello from Docker\n');
    res.sendfile('index.html');
    console.log('CloudUnit reading console log ...' + req.url);
});

// Create reusable transporter object using the default SMTP transport and sending mail function

app.post('/contact', (req, res) => {
    console.log(JSON.stringify(req.body));
    if(req.body.setName == "" || req.body.setEmail == "") {     // check that the requested fields are filled in.
        res.send("Error: Name & Email should not be blank");        // Message if issue in field
        return false;
    }

// setup email data

    let mailOptions = {
        from: "cloudunit@treeptik.com",                         // sender address
        // to: "Treeptik mail - <onifuerte@gmail.com>",         // list of receivers
        to: userMail,                                               // list of receivers
        subject: 'Cloudunit new test part2.',
        html: "<b>" + "Name : " + req.body.setName + "<b>" + "<br>" + "Mail : " + req.body.setEmail   // name to form in index.html
    };

// In case of error

    sendmail(mailOptions, (err, reply) => {
        console.log(err);
        console.dir(reply);
    });

// redirect to cloudunit site to try

    res.redirect("https://cu01.cloudunit.io/#/login");
});

// Starting server

const server = http.createServer(app).listen(port, () => {
    console.log("Server Running on 127.0.0.1 : " + port);
});
